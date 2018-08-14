require xorg-util-common.inc

SUMMARY = "create dependencies in makefiles"

DESCRIPTION = "The makedepend program reads each sourcefile in sequence \
and parses it like a C-preprocessor, processing \
all #include, #define,  #undef, #ifdef, #ifndef, #endif, #if, #elif \
and #else directives so that it can correctly tell which #include, \
directives would be used in a compilation. Any #include, directives \
can reference files having other #include directives, and parsing will \
occur in these files as well."

DEPENDS = "xproto util-macros"
PE = "1"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=43a6eda34b48ee821b3b66f4f753ce4f"

SRC_URI[md5sum] = "efb2d7c7e22840947863efaedc175747"
SRC_URI[sha256sum] = "503903d41fb5badb73cb70d7b3740c8b30fe1cc68c504d3b6a85e6644c4e5004"
