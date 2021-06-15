require xorg-util-common.inc

SUMMARY = "create dependencies in makefiles"

DESCRIPTION = "The makedepend program reads each sourcefile in sequence \
and parses it like a C-preprocessor, processing \
all #include, #define,  #undef, #ifdef, #ifndef, #endif, #if, #elif \
and #else directives so that it can correctly tell which #include, \
directives would be used in a compilation. Any #include, directives \
can reference files having other #include directives, and parsing will \
occur in these files as well."

DEPENDS = "xorgproto util-macros"
PE = "1"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=43a6eda34b48ee821b3b66f4f753ce4f"

SRC_URI[md5sum] = "8a583055c84914060c35b6c2f963fc07"
SRC_URI[sha256sum] = "845f6708fc850bf53f5b1d0fb4352c4feab3949f140b26f71b22faba354c3365"
