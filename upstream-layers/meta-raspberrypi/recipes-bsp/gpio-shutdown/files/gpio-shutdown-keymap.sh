#!/bin/sh
##
# Bind the gpio-shutdown keycode as Keyboard signal and load it to the
# keymap during startup.
##
case "$1" in
    start)
    # Inject the gpio keycode to keymap
    echo "keycode 116 = KeyboardSignal" | loadkeys
    ;;
    *)
    ;;
esac
