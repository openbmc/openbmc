require attr.inc

# configure.ac was missing from the release tarball. This should be fixed in
# future releases of attr, remove this when updating the recipe.
SRC_URI += "file://attr-Missing-configure.ac.patch \
            file://dont-use-decl-macros.patch \
            file://Remove-the-section-2-man-pages.patch \
            file://Remove-the-attr.5-man-page-moved-to-man-pages.patch \
            file://0001-Use-stdint-types-consistently.patch \
           "

SRC_URI[md5sum] = "84f58dec00b60f2dc8fd1c9709291cc7"
SRC_URI[sha256sum] = "25772f653ac5b2e3ceeb89df50e4688891e21f723c460636548971652af0a859"

BBCLASSEXTEND = "native nativesdk"
