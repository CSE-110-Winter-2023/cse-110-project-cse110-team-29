package com.example.socialcompass;

import android.graphics.Rect;
import android.text.TextPaint;

import com.example.socialcompass.model.Friend;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LabelService {

        public void truncateLabels(List<Friend> friends) {
            // sort the friends based on their distance from the center
            Collections.sort(friends, new Comparator<Friend>() {
                @Override
                public int compare(Friend f1, Friend f2) {
                    return Double.compare(f1.distanceFromCenter, f2.distanceFromCenter);
                }
            });

            // check for overlapping labels
            for (int i = 0; i < friends.size() - 1; i++) {
                Friend friend1 = friends.get(i);
                Friend friend2 = friends.get(i + 1);

                // calculate the distance between the two friends' labels
                float[] distance = new float[1];
                Location.distanceBetween(friend1.labelLocation.getLatitude(),
                        friend1.labelLocation.getLongitude(),
                        friend2.labelLocation.getLatitude(),
                        friend2.labelLocation.getLongitude(),
                        distance);

                // if the distance is less than the sum of the label widths, truncate the longer label
                if (distance[0] < friend1.labelWidth + friend2.labelWidth) {
                    if (friend1.labelWidth > friend2.labelWidth) {
                        friend1.truncatedLabel = truncateString(friend1.label, friend2.labelWidth);
                    } else {
                        friend2.truncatedLabel = truncateString(friend2.label, friend1.labelWidth);
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