SUMMARY = "Basic desktop integration functions"

DESCRIPTION = "The xdg-utils package is a set of simple scripts that provide basic \
desktop integration functions for any Free Desktop, such as Linux. \
They are intended to provide a set of defacto standards. \
The following scripts are provided at this time: \
xdg-desktop-icon \     
xdg-desktop-menu \  
xdg-email \ 
xdg-icon-resource \
xdg-mime \       
xdg-open \     
xdg-screensaver \ 
xdg-terminal \
"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a5367a90934098d6b05af3b746405014"

SRC_URI = "http://portland.freedesktop.org/download/${BPN}-${PV}.tar.gz \
           file://0001-Reinstate-xdg-terminal.patch \
          "

SRC_URI[md5sum] = "fadf5e7a08e0526fc60dbe3e5b7ef8d6"
SRC_URI[sha256sum] = "7b05558ae4bb8ede356863cae8c42e3e012aa421bf9d45130a570fd209d79102"

inherit autotools-brokensep distro_features_check

# The xprop requires x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} += "xprop"
