# Handle Glide Vendor Package Management use
#
# Copyright 2018 (C) O.S. Systems Software LTDA.

DEPENDS_append = " glide-native"

do_compile_prepend() {
    ( cd ${B}/src/${GO_IMPORT} && glide install )
}
