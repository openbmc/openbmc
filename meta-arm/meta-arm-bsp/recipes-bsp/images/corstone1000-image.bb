SUMARY = "Corstone1000 platform Image"
DESCRIPTION = "This is the main image which is the container of all the binaries \
               generated for the Corstone1000 platform."
LICENSE = "MIT"

COMPATIBLE_MACHINE = "corstone1000"

inherit image
inherit wic_nopt

PACKAGE_INSTALL = ""

IMAGE_FSTYPES += "wic wic.nopt"
