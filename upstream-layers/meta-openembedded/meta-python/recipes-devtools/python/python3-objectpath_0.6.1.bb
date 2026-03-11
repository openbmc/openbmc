DESCRIPTION = "The agile NoSQL query language for semi-structured data"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=a53cbc7cb75660694e138ba973c148df"

DEPENDS += "dtc-native"

SRC_URI[sha256sum] = "461263136c79292e42431fbb85cdcaac4c6a256f6b1aa5b3ae9316e4965ad819"

inherit pypi setuptools3
