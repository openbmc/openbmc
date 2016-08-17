DESCRIPTION = "Emotion generic players"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a0f11eedf8706bf6d4d6639fb67eb8ae"

DEPENDS = "emotion"

inherit efl

SRCNAME = "emotion_generic_players"

# creates empty packages until you get vlc-2 and enable it here
PACKAGECONFIG ?= ""
# needs eina and ecore from efl and vlc-2 (currently we have 1.11)
PACKAGECONFIG[vlc] = "--with-vlc,--without-vlc,vlc efl"
