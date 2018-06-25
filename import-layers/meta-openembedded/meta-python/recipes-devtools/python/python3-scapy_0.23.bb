DESCRIPTION = "Packet crafting/sending/sniffing, PCAP processing tool,\
based on scapy with python3 compatibility"
SECTION = "devel/python"
HOMEPAGE = "https://github.com/phaethon/scapy"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=95ea6ecfc360eb47fe6f470ad736d7cd"

inherit pypi setuptools3

PYPI_PACKAGE = "scapy-python3"

SRC_URI[md5sum] = "8642d09ca727e7e2b455223ae94059b7"
SRC_URI[sha256sum] = "8760991a67162f43af4d9e64828bcefc100ba88859b75177ae9f7ace56e58186"
