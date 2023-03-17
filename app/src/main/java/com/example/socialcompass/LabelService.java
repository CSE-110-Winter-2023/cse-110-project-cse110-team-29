package com.example.socialcompass;

import android.graphics.Rect;
import android.text.TextPaint;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.socialcompass.model.Friend;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LabelService{
        public static void truncateLabels(List<LocationDisplayer> lds) {
            // sort the friends based on their distance from the center

            Collections.sort(lds, new Comparator<LocationDisplayer>() {
                @Override
                public int compare(LocationDisplayer l1, LocationDisplayer l2) {
                    int r1 = ((ConstraintLayout.LayoutParams)l1.getView().getLayoutParams()).circleRadius;
                    int r2 = ((ConstraintLayout.LayoutParams)l2.getView().getLayoutParams()).circleRadius;
                    return Double.compare(r1, r2);
                }
            });

            for (int i = 1; i < lds.size(); i++) {
                // check for overlapping with earlier labels
                TextView thisView = lds.get(i).getView();

                if (thisView.getText().equals("•")) {
                    continue;
                }

                int[] thisLoc = new int[2];
                int prevOffset = lds.get(i).getOffset();
                lds.get(i).setOffset(0);
                lds.get(i).updateView();
                thisView.getLocationOnScreen(thisLoc);
                lds.get(i).setOffset(prevOffset);
                lds.get(i).updateView();

                int offset = 0;

                for (int j = 0; j < i; j++) {
                    TextView otherView = lds.get(j).getView();
                    int[] otherLoc = new int[2];
                    int prevOffset2 = lds.get(j).getOffset();
                    lds.get(j).setOffset(0);
                    lds.get(j).updateView();
                    otherView.getLocationOnScreen(otherLoc);
                    lds.get(j).setOffset(prevOffset2);
                    lds.get(j).updateView();

                    // calculate horizontal overlap
                    TextView left, right;
                    int[] leftLoc, rightLoc;
                    if (thisLoc[0] < otherLoc[0]) {
                        left = thisView;
                        right = otherView;
                        leftLoc = thisLoc;
                        rightLoc = otherLoc;
                    } else {
                        left = otherView;
                        right = thisView;
                        leftLoc = otherLoc;
                        rightLoc = thisLoc;
                    }
                    int hOverlap = Math.max(0, left.getWidth() - (rightLoc[0] - leftLoc[0]));

                    // calculate vertical overlap
                    int vOverlap;
                    if (thisLoc[1] < otherLoc[1]) {
                        // thisLoc above
                        vOverlap = Math.max(0, thisView.getHeight() - (otherLoc[1] - thisLoc[1]));
                    }
                    else {
                        // otherLoc above
                        vOverlap = Math.max(0, otherView.getHeight() - (thisLoc[1] - otherLoc[1]));
                    }

                    /*
                    var rawAngle = ((ConstraintLayout.LayoutParams)thisView.getLayoutParams()).circleAngle;
                    var angle = Math.toRadians((rawAngle+360+270) % 360);
                    int xRequiredOffset = (int)Math.ceil(hOverlap / Math.cos(angle));
                    int yRequiredOffset = (int)Math.ceil(vOverlap / Math.sin(angle));
                    offset = Math.max(offset, Math.max(xRequiredOffset, yRequiredOffset)); */
                    offset = Math.max(offset, 50);
                }

                lds.get(i).setOffset(offset);
                lds.get(i).updateView();
            }
            /*
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
            }*/
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