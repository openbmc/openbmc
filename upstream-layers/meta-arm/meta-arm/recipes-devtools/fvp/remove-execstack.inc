# Clear the executable stack flag on named shared libraries. This are typically
# not needed, and glibc 2.41 will refuse to dlopen() a library that expects
# an executable stack.
#
# The stack permissions can be checked with readelf -lW <.so> | grep GNU_STACK.
# RW is read/write, RWE is read/write/execute.

DEPENDS += "execstack-native"

REMOVE_EXECSTACKS ?= ""

do_install:append() {
    for SO in ${REMOVE_EXECSTACKS}; do
        NAME=$(basename $SO)
        SO=${D}$SO

        test -f $SO || bbfatal remove-execstack: cannot find $SO

        if execstack --query $SO | grep -q ^X; then
            bbnote "Stripping executable stack bit on $NAME"
            execstack --clear-execstack $SO
        else
            bbwarn "Executable stack stripping no longer required for $NAME, remove"
        fi
    done
}
