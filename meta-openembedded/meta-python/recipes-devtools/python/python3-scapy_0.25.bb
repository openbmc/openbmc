DESCRIPTION = "Packet crafting/sending/sniffing, PCAP processing tool,\
based on scapy with python3 compatibility"
SECTION = "devel/python"
HOMEPAGE = "https://github.com/phaethon/scapy"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=a4282d4d80227fa599a99e77fdd95e71"

inherit pypi setuptools3

PYPI_PACKAGE = "scapy-python3"

SRC_URI[md5sum] = "c9003d39def73c028cb8c71bcbb42629"
SRC_URI[sha256sum] = "2ae1b3bd9759844e830a6cc3ba11c3f25b08433a8ee3e7eddc08224905e5ef2b"
