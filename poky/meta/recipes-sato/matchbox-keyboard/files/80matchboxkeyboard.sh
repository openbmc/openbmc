#!/bin/sh

. /etc/formfactor/config

CMD=""

if [ "$HAVE_KEYBOARD" = "0" ]; then
    CMD="matchbox-keyboard -d"
elif [ "$DISPLAY_CAN_ROTATE" = "1" ]; then
    if [ "$HAVE_KEYBOARD_PORTRAIT" = "1" -a "$HAVE_KEYBOARD_LANDSCAPE" = "0" ]; then
        CMD="matchbox-keyboard -d -o landscape"
    elif [ "$HAVE_KEYBOARD_LANDSCAPE" = "1" -a "$HAVE_KEYBOARD_PORTRAIT" = "0" ]; then
        CMD="matchbox-keyboard -d -o portrait"
    fi
fi

if [ "$CMD" ]; then
    # Delay to make sure the window manager is active
    # by waiting for the desktop to say its finished loading
    dbus-wait org.matchbox_project.desktop Loaded && $CMD &
fi
