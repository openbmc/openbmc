SUMMARY = "Object-Oriented HTTP framework"
DESCRIPTION = "CherryPy is a pythonic, object-oriented HTTP framework"
HOMEPAGE = "http://www.cherrypy.org/"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://cherrypy/LICENSE.txt;md5=a476d86a3f85c89411ecaad012eed1e3"

inherit allarch
inherit setuptools

PROVIDES+="${PN}-tests ${PN}-tutorials ${PN}-server ${PN}-scaffold ${PN}-daemon ${PN}-docs"
PACKAGES+="${PN}-tests ${PN}-tutorials ${PN}-server ${PN}-scaffold ${PN}-daemon ${PN}-docs"

base_package_dir = "${libdir}/python2.7/site-packages"
package_dir = "${base_package_dir}/cherrypy"
share_dir = "${datadir}/cherrypy"

SUMMARY_${PN}-server="${BPN} server"
RDEPENDS_${PN}-server="${PN}"
FILES_${PN}-server="${package_dir}/wsgiserver ${share_dir}/wsgiserver "

SUMMARY_${PN}-scaffold="${BPN} scaffold"
RDEPENDS_${PN}-scaffold="${PN}"
FILES_${PN}-scaffold="${package_dir}/scaffold ${share_dir}/scaffold "

SUMMARY_${PN}-tests="${BPN} tests"
RDEPENDS_${PN}-tests="${PN}"
FILES_${PN}-tests="${package_dir}/test ${share_dir}/test "

SUMMARY_${PN}-tutorials ="${BPN} tutorials"
RDEPENDS_${PN}-tutorials ="${PN}"
FILES_${PN}-tutorials ="${package_dir}/tutorial ${share_dir}/tutorial "

SUMMARY_${PN}-daemon ="${BPN} daemon"
RDEPENDS_${PN}-daemon ="${PN}"
FILES_${PN}-daemon ="${bindir} ${share_dir}/cherryd "

SUMMARY_${PN}-docs ="${BPN} docs"
RDEPENDS_${PN}-docs ="${PN}"
FILES_${PN}-docs ="${share_dir}/favicon.ico ${share_dir}/LICENSE.txt "

FILES_${PN} = " \
        ${package_dir}/*.py* \
        ${package_dir}/lib \
        ${package_dir}/process \
        ${share_dir}/process \
        ${base_package_dir}/*.egg-info \
	"
BBCLASSEXTEND = "nativesdk"
