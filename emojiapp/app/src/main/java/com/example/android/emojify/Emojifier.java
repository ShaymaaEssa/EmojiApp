/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    private static final double eyeOpenProp = 0.15;
    private static final double smilingProp = 0.15;
    private static final float EMOJI_SCALE_FACTOR = .9f;
    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap picture) {

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
        Log.d(LOG_TAG, "detectFacesAndOverlayEmoji: number of faces = " + faces.size());

        // If there are no faces detected, show a Toast message
        Bitmap resultBitmap = picture;
        if(faces.size() == 0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                // Log the classification probabilities for each face.
                Bitmap emojiBitmap;
                Emoji emoji= whichEmoji(face);
                // TODO (6): Change the call to whichEmoji to whichEmoji() to log the appropriate emoji for the facial expression.

                switch (emoji){
                    case Smiling:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.smile);
                        break;
                    case Frowning:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.frown);
                        break;
                    case ClosedEyeSmiling:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.closed_smile);
                        break;
                    case CloseEyeFrowning:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.closed_frown);
                        break;
                    case LeftWink:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.leftwink);
                        break;
                    case RightWink:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.rightwink);
                        break;
                    case RightWinkFrowning:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.rightwinkfrown);
                        break;
                    case LeftWinkFrowning:  emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.leftwinkfrown);
                        break;
                    default: emojiBitmap = null;
                }

                resultBitmap = addBitmapToFace(picture, resultBitmap, face);
            }

        }


        // Release the detector
        detector.release();
        return resultBitmap;

    }


    /**
     * Method for logging the classification probabilities.
     *
     * @param face The face to get the classification probabilities.
     */
    private static Emoji whichEmoji(Face face){
        // TODO (2): Change the name of the whichEmoji() method to whichEmoji() (also change the log statements)
        // Log all the probabilities
        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());

        // TODO (3): Create threshold constants for a person smilingProp, and and eye being open by taking pictures of yourself and your friends and noting the logs.
        // TODO (4): Create 3 boolean variables to track the state of the facial expression based on the thresholds you set in the previous step: smilingProp, left eye closed, right eye closed.
        boolean isLeftEyeOpen = face.getIsLeftEyeOpenProbability()>= eyeOpenProp;
        boolean isRightEyeOpen = face.getIsRightEyeOpenProbability() > eyeOpenProp;
        boolean isSmiling = face.getIsSmilingProbability() > smilingProp;
        // TODO (5): Create an if/else system that selects the appropriate emoji based on the above booleans and log the result.
        Emoji emoji;

        if (isLeftEyeOpen && isRightEyeOpen && isSmiling)
            emoji = Emoji.Smiling;
        else if (isLeftEyeOpen && isRightEyeOpen && !isSmiling)
            emoji = Emoji.Frowning;
        else if (!isLeftEyeOpen && isRightEyeOpen && isSmiling)
            emoji = Emoji.LeftWink;
        else if (!isLeftEyeOpen && isRightEyeOpen && !isSmiling)
            emoji = Emoji.LeftWinkFrowning;
        else if (isLeftEyeOpen && !isRightEyeOpen && isSmiling)
            emoji = Emoji.RightWink;
        else if (isLeftEyeOpen && !isRightEyeOpen && !isSmiling)
            emoji = Emoji.RightWinkFrowning;
        else if (!isLeftEyeOpen && !isRightEyeOpen && isSmiling)
            emoji = Emoji.ClosedEyeSmiling;
        else if (!isLeftEyeOpen && !isRightEyeOpen && !isSmiling)
            emoji = Emoji.CloseEyeFrowning;
        else emoji = Emoji.NotDefined;

        return emoji;
    }


    // TODO (1): Create an enum class called Emoji that contains all the possible emoji you can make (smilingProp, frowning, left wink, right wink, left wink frowning, right wink frowning, closed eye smilingProp, close eye frowning).

    public enum Emoji {
        Smiling,
        Frowning,
        LeftWink,
        RightWink,
        LeftWinkFrowning,
        RightWinkFrowning,
        ClosedEyeSmiling,
        CloseEyeFrowning,
        NotDefined
    }

    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }
}
