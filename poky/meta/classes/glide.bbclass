# Handle Glide Vendor Package Management use
#
# Copyright 2018 (C) O.S. Systems Software LTDA.

DEPENDS:append = " glide-native"

do_compile:prepend() {
    ( cd ${B}/src/${GO_IMPORT} && glide install )
}
