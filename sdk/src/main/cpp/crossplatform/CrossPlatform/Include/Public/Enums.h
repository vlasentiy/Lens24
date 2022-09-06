//
//  Enums.h
//  CardRecognizer
//
//  Created by Vladimir Tchernitski on 16/03/16.
//  Copyright © 2016 Vladimir Tchernitski. All rights reserved.
//

#ifndef Enums_h
#define Enums_h

typedef enum Lens24RecognizerOrientation {
    Lens24RecognizerOrientationUnknown = 0,
    Lens24RecognizerOrientationPortrait = 1,
    Lens24RecognizerOrientationPortraitUpsideDown = 2,
    Lens24RecognizerOrientationLandscapeRight = 3,
    Lens24RecognizerOrientationLandscapeLeft = 4
} Lens24RecognizerOrientation;

typedef enum Lens24RecognizerMode {
    Lens24RecognizerModeNone = 0,
    Lens24RecognizerModeNumber = 1,
    Lens24RecognizerModeDate = 2,
    Lens24RecognizerModeName = 4,
    Lens24RecognizerModeGrabCardImage = 8
} Lens24RecognizerMode;


#endif /* Enums_h */
