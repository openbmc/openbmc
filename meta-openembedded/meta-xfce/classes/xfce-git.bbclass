do_configure_prepend() {
       cd ${S}
       NOCONFIGURE=yes ./autogen.sh
       cd ${B}
}

AUTOTOOLS_COPYACLOCAL = "1"
