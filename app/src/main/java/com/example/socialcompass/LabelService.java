package com.example.socialcompass;

import android.graphics.Rect;
import android.text.TextPaint;

import com.example.socialcompass.model.Friend;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LabelService extends CircularActivity{
        LocationService myLocation = getLocationService();
        public void truncateLabels(List<Friend> friends) {
            // sort the friends based on their distance from the center

            Collections.sort(friends, new Comparator<Friend>() {
                @Override
                public int compare(Friend f1, Friend f2) {
                    double f1Dis = Utilities.distance(f1.getLatitude(),
                            f1.getLongitude(),
                            myLocation.getLocation().getValue().first,
                            myLocation.getLocation().getValue().second);
                    double f2Dis = Utilities.distance(f2.getLatitude(),
                            f2.getLongitude(),
                            myLocation.getLocation().getValue().first,
                            myLocation.getLocation().getValue().second);

                    return Double.compare(f1Dis, f2Dis);
                }
            });

            // check for overlapping labels
            for (int i = 0; i < friends.size() - 1; i++) {
                Friend friend1 = friends.get(i);
                Friend friend2 = friends.get(i + 1);

                // calculate the distance between the two friends' labels
                double[] distance = new double[1];
                distance [0] = Utilities.distance(friend1.getLatitude(),
                        friend1.getLongitude(),
                        friend2.getLatitude(),
                        friend2.getLongitude()
                        );

                // if the distance is less than the sum of the label widths, truncate the longer label
                if (distance[0] < friend1.getName().length() + friend2.getName().length()) {
                    if (friend1.getName().length() > friend2.getName().length()) {
                        friend1.setFriend_name((truncateString(friend1.getName(), friend2.getName().length())));
                    } else {
                        friend2.setFriend_name((truncateString(friend2.getName(), friend1.getName().length())));
                    }
                }
            }
        }

        // helper method to truncate a string to a specified width
        public String truncateString(String str, float width) {
            TextPaint paint = new TextPaint();
            Rect bounds = new Rect();
            paint.getTextBounds(str, 0, str.length(), bounds);
            float ellipsisWidth = paint.measureText("...");

            if (width < bounds.width()) {
                // calculate the maximum number of characters that can fit in the given width
                int maxChars = paint.breakText(str, true, width - ellipsisWidth, null);
                return str.substring(0, maxChars) + "...";
            } else {
                return str;
            }
        }
    }