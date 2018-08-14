SUMMARY = "Diffutils contains tools used for finding differences between files"
HOMEPAGE = "https://www.gnu.org/software/diffutils/diffutils.html"
DESCRIPTION = "Diffutils contains the GNU diff, diff3, \
sdiff, and cmp utilities. These programs are usually \
used for creating patch files."
SECTION = "base"

inherit autotools texinfo update-alternatives gettext

ALTERNATIVE_${PN} = "diff cmp"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native nativesdk"
