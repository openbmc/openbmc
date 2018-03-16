DESCRIPTION = " *************************** CAUTION \
**************************************	*			        \
					 * \
 * INCOMPATIBLE CHANGE (JSON::XS version 2.90)			        \
  * \
 *								        \
  * \
 * JSON.pm had patched JSON::XS::Boolean and JSON::PP::Boolean \
internally * \
 * on loading time for making these modules inherit JSON::Boolean.      \
  * \
 * But since JSON::XS v3.0 it use Types::Serialiser as boolean class.   \
  * \
 * Then now JSON.pm breaks boolean classe overload features and         \
  * \
 * -support_by_pp if JSON::XS v3.0 or later is installed.	        \
  * \
 *								        \
  * \
 * JSON::true and JSON::false returned JSON::Boolean objects.	        \
  * \
 * For workaround, they return JSON::PP::Boolean objects in this \
version. * \
 *								        \
  * \
 *     isa_ok(JSON::true, 'JSON::PP::Boolean'); 		        \
  * \
 *								        \
  * \
 * And it discards a feature:					        \
  * \
 *								        \
  * \
 *     ok(JSON::true eq 'true');				        \
  * \
 *								        \
  * \
 * In other word, JSON::PP::Boolean overload numeric only.	        \
  * \
 *								        \
  * \
 *     ok( JSON::true == 1 );					        \
  * \
 *								        \
  * \
 \
*********************************************************************** \
***"

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-2.0"
PR = "r0"

MAINTAINER=	"Poky <poky@yoctoproject.org>"
HOMEPAGE=	"https://metacpan.org/release/JSON"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Artistic-1.0;md5=cda03bbdc3c1951996392b872397b798 \
file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "http://cpan.metacpan.org/authors/id/M/MA/MAKAMAKA/JSON-2.90.tar.gz"

SRC_URI[md5sum] = "e1512169a623e790a3f69b599cc1d3b9"
SRC_URI[sha256sum] = "4ddbb3cb985a79f69a34e7c26cde1c81120d03487e87366f9a119f90f7bdfe88"

S = "${WORKDIR}/JSON-${PV}"

inherit cpan

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
