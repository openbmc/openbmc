require recipes-core/packagegroups/nativesdk-packagegroup-sdk-host.bb

DESCRIPTION = "Host packages for Qt Embedded SDK"
LICENSE = "MIT"

RDEPENDS_${PN} += "nativesdk-qt4-tools"
