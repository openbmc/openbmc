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

SRC_URI[sha256sum] = "bc94ffda6cd4671603a69c39dbe8f96b317707b9185b2aaa3b54b5d134b41884"
