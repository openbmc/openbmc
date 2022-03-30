SUMMARY = "Set of CLI tools for Openlmi providers"
DESCRIPTION = "openlmi-tools is a set of command line tools for Openlmi providers."
HOMEPAGE = "http://www.openlmi.org/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://../COPYING;md5=75859989545e37968a99b631ef42722e"
SECTION = "System/Management"

inherit ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "setuptools", "", d)}

SKIP_RECIPE[openlmi-tools] ?= "${@bb.utils.contains('I_SWEAR_TO_MIGRATE_TO_PYTHON3', 'yes', '', 'python2 is out of support for long time, read https://www.python.org/doc/sunset-python-2/ https://python3statement.org/ and if you really have to temporarily use this, then set I_SWEAR_TO_MIGRATE_TO_PYTHON3 to "yes"', d)}"

DEPENDS = "python-native python-pywbem-native python-m2crypto python-pywbem"

SRC_URI = "http://fedorahosted.org/released/${BPN}/${BP}.tar.gz \
          "
SRC_URI[md5sum] = "e156246cb7b49753db82f4ddf7f03e50"
SRC_URI[sha256sum] = "292b8f5f2250655a4add8183c529b73358bc980bd4f23cfa484a940953fce9e4"

S = "${WORKDIR}/${BP}/cli"

do_configure:prepend() {
    sed 's/@@VERSION@@/$(VERSION)/g' ${S}/setup.py.skel > ${S}/setup.py
}

python() {
    if 'meta-python2' not in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('Requires meta-python2 to be present.')
}
