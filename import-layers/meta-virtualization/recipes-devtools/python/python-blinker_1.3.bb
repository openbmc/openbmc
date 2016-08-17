HOMEPAGE = "https://pypi.python.org/pypi/blinker"
SUMMARY = "Fast, simple object-to-object and broadcast signaling"
DESCRIPTION = " \
  Blinker provides a fast dispatching system that allows any number of \
  interested parties to subscribe to events, or “signals”. \
  . \
  Signal receivers can subscribe to specific senders or receive signals \
  sent by any sender. \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8baf1d53a00de619f60052e4752a89af"

SRCNAME = "blinker"
SRC_URI = "https://pypi.python.org/packages/source/b/blinker/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "66e9688f2d287593a0e698cd8a5fbc57"
SRC_URI[sha256sum] = "6811010809262261e41ab7b92f3f6d23f35cf816fbec2bc05077992eebec6e2f"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

