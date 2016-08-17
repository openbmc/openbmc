SUMMARY = "Creates metadata indexes for RPM package repositories"
HOMEPAGE = "http://createrepo.baseurl.org/"

RECIPE_NO_UPDATE_REASON = "Versions after 0.9.* use YUM, so we hold at 0.4.11"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

DEPENDS_class-native += "libxml2-native rpm-native"
RDEPENDS_${PN}_class-target = "libxml2-python"

PR = "r9"

SRC_URI = "http://createrepo.baseurl.org/download/${BP}.tar.gz \
           file://fix-native-install.patch \
           file://python-scripts-should-use-interpreter-from-env.patch \
           file://createrepo-rpm549.patch \
           file://recommends.patch \
           file://createrepo-dbpath.patch \
           file://dumpMetadata-disable-signature-validation.patch \
           file://rpm-createsolvedb.py \
           file://fixstat.patch \
           "

SRC_URI[md5sum] = "3e9ccf4abcffe3f49af078c83611eda2"
SRC_URI[sha256sum] = "a73ae11a0dcde8bde36d900bc3f7f8f1083ba752c70a5c61b72d1e1e7608f21b"

BBCLASSEXTEND = "native"

do_install () {
	oe_runmake -e 'DESTDIR=${D}' install
	install -m 0755 ${WORKDIR}/rpm-createsolvedb.py ${D}${bindir}/
}

# Wrap the python script since the native python is
# ${bindir}/python-native/python, and the "#! /usr/bin/env python" can't
# find it since it is not in PATH.
do_install_append_class-native () {
	# Not all the python scripts should be wrapped since some of
	# them are modules (be imported).
	for i in ${D}${datadir}/createrepo/genpkgmetadata.py \
		 ${D}${datadir}/createrepo/modifyrepo.py \
		 ${D}${bindir}/rpm-createsolvedb.py ; do
		sed -i -e 's|^#!.*/usr/bin/env python|#! /usr/bin/env nativepython|' $i
	done

	create_wrapper ${D}/${bindir}/createrepo \
			RPM_USRLIBRPM=${STAGING_LIBDIR_NATIVE}/rpm \
			RPM_ETCRPM=${STAGING_ETCDIR_NATIVE}/rpm \
			RPM_LOCALEDIRRPM=${STAGING_DATADIR_NATIVE}/locale
}
