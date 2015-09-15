# package_regex.inc - This file contains data that tracks
# upstream project associated with a given recipe. This list is needed
# for recipes that version information can not be automagically discovered.
# As we automate this checking, this file will slowly be reduced.
#"
# This data is used by the package reporting system (packages.yoctoproject.org)
#"
# The format is as a bitbake variable override for each recipe
#"
#       REGEX_URI_pn-<recipe name> = "recipe_url"
#	 - This is the url used by the package checking system to
#	   get the latest version of the package
#       REGEX_pn-<recipe name> = "package_regex"
#	 - This is the regex the package checking system uses to
#	   parse the page found at REGEX_URI_pn-<recipe name>
#	GITTAGREGEX_pn-<recipe name> = "git_tag_regex"
#	 - When source code is fetched from git, git tags are used to
#	   determine the upstream release version. This regex can be used
#	   to filter only relevant tags.
#

COMMON_REGEX = "(?P<pver>(\d+[\.\-_]*)+)"

# Generic regex don't match
REGEX_URI_pn-wireless-tools = "http://www.hpl.hp.com/personal/Jean_Tourrilhes/Linux/Tools.html"
REGEX_pn-wireless-tools = "wireless_tools\.(?P<pver>(\d+)(\..*|))\.tar\.gz"

REGEX_URI_pn-sqlite3="http://www.sqlite.org/"
REGEX_pn-sqlite3="releaselog/${COMMON_REGEX}.html"

REGEX_URI_pn-dhcp = "ftp://ftp.isc.org/isc/dhcp/"
REGEX_pn-dhcp="(?P<pver>\d+\.\d+\.(\d+?))/"

REGEX_pn-foomatic-filters = "foomatic-filters-(?P<pver>((\d|\d\d)\.*)+)\.tar\.gz"
REGEX_pn-xdg-utils = "xdg-utils-(?P<pver>((\d+[\.\-_]*)+)((rc|alpha|beta)\d+)?)\.(tar\.gz|tgz)"
REGEX_pn-rpm="rpm-${COMMON_REGEX}-.*$"
REGEX_pn-libtheora = "libtheora-(?P<pver>\d+(\.\d)+)\.(tar\.gz|tgz)"

REGEX_pn-iputils="iputils-(?P<pver>s\d+).tar"
REGEX_pn-nettle = "nettle-(?P<pver>\d+(\.\d+)+)\.tar"

REGEX_pn-cairo = "cairo-(?P<pver>\d+(\.\d+)+)\.tar"
REGEX_pn-cmake = "cmake-(?P<pver>\d+(\.\d+)+)\.tar"
REGEX_pn-libsdl = "SDL-(?P<pver>\d+(\.\d+)+)\.tar"
REGEX_pn-libxslt = "libxslt-(?P<pver>\d+(\.\d+)+)\.tar"

# Exclude NC versions which lack AES encryption
REGEX_pn-db = "db-(?P<pver>\d+\.\d+(\.\d+)?).tar"

REGEX_URI_pn-autogen = "http://ftp.gnu.org/gnu/autogen/"
REGEX_pn-autogen = "rel(?P<pver>\d+(\.\d+)+)/"

# python recipe is actually python 2.x
# also, exclude pre-releases for both python 2.x and 3.x
REGEX_pn-python = "[Pp]ython-(?P<pver>2(\.\d+)+).tar"
REGEX_pn-python3 = "[Pp]ython-(?P<pver>\d+(\.\d+)+).tar"

# exclude betas
REGEX_pn-tiff = "tiff-(?P<pver>\d+(\.\d+)+).tar"

# exclude version 5.5.2 which triggers a false positive
REGEX_pn-unzip = "unzip(?P<pver>(?!552).+)\.tgz"
# similar for zip
REGEX_pn-zip = "^zip(?P<pver>(?!232).+)\.tgz"

# Isn't possible to download with the default URI web server returns
# (403, 404, 550)
REGEX_URI_pn-dosfstools = "https://github.com/dosfstools/dosfstools/releases"
REGEX_pn-json-c = "json-c-(?P<pver>\d+(\.\d+)+).tar"
# json-c releases page is fetching the list of releases in some weird XML format
# from https://s3.amazonaws.com/json-c_releases and processes it with javascript :-/
#REGEX_URI_pn-json-c = "https://s3.amazonaws.com/json-c_releases/releases/index.html"
REGEX_URI_pn-chrpath = "http://alioth.debian.org/frs/?group_id=31052"

REGEX_URI_pn-powertop = "https://01.org/powertop/downloads"
REGEX_URI_pn-libsamplerate0 = "http://www.mega-nerd.com/SRC/download.html"
REGEX_URI_pn-libxkbcommon = "http://xkbcommon.org/"
REGEX_URI_pn-tslib = "https://github.com/kergoth/tslib/releases"
REGEX_URI_pn-waffle="http://www.waffle-gl.org/releases.html"
REGEX_URI_pn-lrzsz = "http://ohse.de/uwe/software/lrzsz.html"
REGEX_URI_pn-libacpi = "http://www.ngolde.de/libacpi.html"
REGEX_URI_pn-less  = "http://www.greenwoodsoftware.com/less/download.html"
REGEX_URI_pn-cups = "http://www.cups.org/software.php"
REGEX_pn-cups = "cups-(?P<pver>\d+\.\d+(\.\d+)?)-source.tar"
REGEX_URI_pn-bzip2 = "http://www.bzip.org/downloads.html"
REGEX_URI_pn-minicom = "https://alioth.debian.org/frs/?group_id=30018"
REGEX_URI_pn-sysstat = "http://sebastien.godard.pagesperso-orange.fr/download.html"
REGEX_URI_pn-libical = "https://github.com/libical/libical/releases/"
REGEX_URI_pn-libmpc = "http://www.multiprecision.org/index.php?prog=mpc&page=download"
REGEX_URI_pn-taglib = "http://github.com/taglib/taglib/releases/"
REGEX_URI_pn-libevent = "http://libevent.org/"
REGEX_URI_pn-db = "http://www.oracle.com/technetwork/products/berkeleydb/downloads/index-082944.html"
REGEX_URI_pn-python-smartpm="https://launchpad.net/smart/trunk/"
REGEX_URI_pn-lsof = "ftp://sunsite.ualberta.ca/pub/Mirror/lsof/"
REGEX_URI_pn-pbzip2 = "http://compression.ca/pbzip2/"
REGEX_URI_pn-quicky = "http://qt-apps.org/content/download.php?content=80325&id=1&tan=10417482"
REGEX_URI_pn-libwebp = "http://downloads.webmproject.org/releases/webp/index.html"

# The engine tries to use directory versioning but is a false-positive
REGEX_URI_pn-intltool = "https://launchpad.net/intltool/trunk/"
REGEX_URI_pn-libnfsidmap = "http://www.citi.umich.edu/projects/nfsv4/linux/libnfsidmap/"
REGEX_URI_pn-mpfr = "http://www.mpfr.org/mpfr-current/"
REGEX_URI_pn-nss = "https://developer.mozilla.org/en-US/docs/Mozilla/Projects/NSS/NSS_Releases"
REGEX_pn-nss = "NSS_(?P<pver>.+)_release_notes"

REGEX_URI_pn-docbook-sgml-dtd-3.1 = "http://www.docbook.org/sgml/"
REGEX_pn-docbook-sgml-dtd-3.1 = "(?P<pver>3\..+)/"
REGEX_URI_pn-docbook-sgml-dtd-4.1 = "http://www.docbook.org/sgml/"
REGEX_pn-docbook-sgml-dtd-4.1 = "(?P<pver>4\.1(\.\d+)*)/"

# Qt recipes should be kept at 4.x
REGEX_URI_pn-qt4-embedded = "http://download.qt.io/official_releases/qt/4.8/"
REGEX_pn-qt4-embedded = "(?P<pver>\d+(\.\d+)+)/"
REGEX_URI_pn-qt4-x11-free = "http://download.qt.io/official_releases/qt/4.8/"
REGEX_pn-qt4-x11-free = "(?P<pver>\d+(\.\d+)+)/"
REGEX_URI_pn-nativesdk-qt4-tools = "http://download.qt.io/official_releases/qt/4.8/"
REGEX_pn-nativesdk-qt4-tools = "(?P<pver>\d+(\.\d+)+)/"
REGEX_URI_pn-qt4-native = "http://download.qt.io/official_releases/qt/4.8/"
REGEX_pn-qt4-native = "(?P<pver>\d+(\.\d+)+)/"

# PyPI sites
REGEX_URI_pn-python-nose = "https://pypi.python.org/pypi/nose/"
REGEX_pn-python-nose = "/nose/${COMMON_REGEX}"
# python-distribute 0.7.x is a simple compatibility layer that installs Setuptools 0.7+.
# so we stick to 0.6.x series
REGEX_URI_pn-python-distribute = "https://pypi.python.org/pypi/distribute/"
REGEX_pn-python-distribute = "/distribute/(?P<pver>\d+.6.\d+)"
REGEX_URI_pn-python-gitdb = "https://pypi.python.org/pypi/gitdb/"
REGEX_URI_pn-python-async = "https://pypi.python.org/pypi/async/"
REGEX_URI_pn-python-smmap = "https://pypi.python.org/pypi/smmap/"
REGEX_pn-python-smmap = "/smmap/${COMMON_REGEX}"
REGEX_URI_pn-python-git = "https://pypi.python.org/pypi/GitPython/"
REGEX_pn-python-git = "/GitPython/${COMMON_REGEX}"
REGEX_URI_pn-python-mako = "https://pypi.python.org/pypi/mako/"
REGEX_pn-python-mako = "/Mako/${COMMON_REGEX}"
REGEX_URI_pn-python-pexpect = "https://pypi.python.org/pypi/pexpect"
REGEX_URI_pn-python3-pip = "https://pypi.python.org/pypi/pip"
REGEX_URI_pn-python3-setuptools = "https://pypi.python.org/pypi/setuptools"

# Sourceforge sites
REGEX_URI_pn-python-scons = "http://sourceforge.net/projects/scons/files/scons/"
REGEX_pn-python-scons = "/scons/${COMMON_REGEX}/"
REGEX_URI_pn-expect = "http://sourceforge.net/projects/expect/files/Expect/"
REGEX_pn-expect = "/Expect/${COMMON_REGEX}/"
REGEX_URI_pn-docbook-xsl-stylesheets = "http://sourceforge.net/projects/docbook/files/docbook-xsl/"
REGEX_pn-docbook-xsl-stylesheets = "/docbook-xsl/${COMMON_REGEX}/"
REGEX_URI_pn-glew = "http://sourceforge.net/projects/glew/files/glew"
REGEX_pn-glew = "/glew/${COMMON_REGEX}/"
REGEX_URI_pn-menu-cache = "http://sourceforge.net/projects/lxde/files/menu-cache/1.0/"
REGEX_URI_pn-x11vnc = "http://sourceforge.net/projects/libvncserver/files/x11vnc/"
REGEX_pn-x11vnc = "/x11vnc/${COMMON_REGEX}/"
REGEX_URI_pn-quota = "http://sourceforge.net/projects/linuxquota/files/quota-tools/"
REGEX_pn-quota = "/quota-tools/${COMMON_REGEX}/"
REGEX_URI_pn-lsb = "http://sourceforge.net/projects/lsb/files/lsb_release/"
REGEX_pn-lsb = "/lsb_release/${COMMON_REGEX}/"
REGEX_URI_pn-libpcre = "http://sourceforge.net/projects/pcre/files/pcre/"
REGEX_pn-libpcre = "/pcre/${COMMON_REGEX}/"
REGEX_URI_pn-libusb-compat = "http://sourceforge.net/projects/libusb/files/libusb-compat-0.1/"
REGEX_pn-libusb-compat = "/libusb-compat-${COMMON_REGEX}/$"
REGEX_URI_pn-sysfsutils = "http://sourceforge.net/projects/linux-diag/files/sysfsutils/"
REGEX_pn-sysfsutils = "/sysfsutils/${COMMON_REGEX}/"
REGEX_URI_pn-console-tools = "http://sourceforge.net/projects/lct/files/console-tools-devel/"
REGEX_pn-console-tools = "/console-tools-devel/(?P<pver>(\d\d?\.)+\d\d?)/"
REGEX_URI_pn-watchdog = "http://sourceforge.net/projects/watchdog/files/watchdog/"
REGEX_pn-watchdog = "/watchdog/${COMMON_REGEX}/"
REGEX_URI_pn-cracklib = "http://sourceforge.net/projects/cracklib/files/cracklib/"
REGEX_pn-cracklib = "/cracklib/${COMMON_REGEX}/"
REGEX_URI_pn-gptfdisk = "http://sourceforge.net/projects/gptfdisk/files/gptfdisk/"
REGEX_pn-gptfdisk = "/gptfdisk/${COMMON_REGEX}/"
REGEX_URI_pn-libpng12 = "http://sourceforge.net/projects/libpng/files/libpng12/"
REGEX_pn-libpng12 = "/libpng12/${COMMON_REGEX}/"
REGEX_URI_pn-docbook-dsssl-stylesheets = "http://sourceforge.net/projects/docbook/files/docbook-dsssl/"
REGEX_pn-docbook-dsssl-stylesheets = "/docbook-dsssl/${COMMON_REGEX}/"

REGEX_URI_pn-openjade = "http://openjade.sourceforge.net/download.html"
REGEX_URI_pn-libpfm4 = "http://sourceforge.net/projects/perfmon2/files/libpfm4/"
REGEX_URI_pn-liba52 = "http://liba52.sourceforge.net/downloads.html"
REGEX_URI_pn-mpeg2dec = "http://libmpeg2.sourceforge.net/downloads.html"
REGEX_URI_pn-pcmanfm = "http://sourceforge.net/projects/pcmanfm/files/PCManFM%20%2B%20Libfm%20%28tarball%20release%29/PCManFM/"
REGEX_URI_pn-libcgroup = "http://sourceforge.net/projects/libcg/files/libcgroup/"
REGEX_URI_pn-cdrtools = "http://sourceforge.net/projects/cdrtools/files/alpha/"

# GIT
GITTAGREGEX_COMMON = "(?P<pver>\d+\.\d+(\.\d+)*)"

GITTAGREGEX_pn-linux-yocto = "${GITTAGREGEX_COMMON}"
GITTAGREGEX_pn-linux-yocto-tiny = "${GITTAGREGEX_COMMON}"
GITTAGREGEX_pn-linux-yocto-dev = "${GITTAGREGEX_COMMON}"
GITTAGREGEX_pn-uclibc = "${GITTAGREGEX_COMMON}"
GITTAGREGEX_pn-glibc = "${GITTAGREGEX_COMMON}"
GITTAGREGEX_pn-cross-localedef = "${GITTAGREGEX_COMMON}"
GITTAGREGEX_pn-binutils = "binutils-(?P<pver>\d+_(\d_?)*)"
GITTAGREGEX_pn-bootchart2 = "${GITTAGREGEX_COMMON}"
GITTAGREGEX_pn-remake = "(?P<pver>(\d+(\.\d+)+)\+dbg.+)"
GITTAGREGEX_pn-tcf-agent = "(?P<pver>(\d+(\.\d+)+))"
GITTAGREGEX_pn-chkconfig-alternatives = "chkconfig-(?P<pver>(\d+(\.\d+)+))"
GITTAGREGEX_pn-sgmlspl = "(?P<pver>(\d+(\.\d+)+))"

# Exclude x.99.x versions
GITTAGREGEX_pn-mx-1.0 = "(?P<pver>^\d+(\.(?!99)\d+)+)"

# Blacklist a bogus tag
GITTAGREGEX_pn-file = "FILE(?P<pver>(?!6_23).+)"
GITTAGREGEX_pn-xinetd = "xinetd-(?P<pver>(?!20030122).+)"

# Blacklist debian-specific tags
GITTAGREGEX_pn-xf86-video-omapfb = "(?P<pver>\d+(\.\d+)+)(?!-)"

# GNOME related.
#
# GNOME2_STABLE picks stable releases in the 2.x series (so not 2.90 onwards,
# which were GNOME 3 betas.
#
# GNOME_STABLE picks the latest stable release.

GNOME2_STABLE = "(?P<pver>2\.([0-8]*[02468])+(\.\d+)+)"
GNOME_STABLE = "(?P<pver>\d+\.(\d*[02468])+(\.\d+)+)"

REGEX_pn-glib-2.0 = "${GNOME_STABLE}"
REGEX_pn-atk = "${GNOME_STABLE}"
REGEX_pn-at-spi2-core = "${GNOME_STABLE}"
REGEX_pn-at-spi2-atk = "${GNOME_STABLE}"
REGEX_pn-gdk-pixbuf = "${GNOME_STABLE}"
REGEX_pn-pango = "${GNOME_STABLE}"
REGEX_pn-gtk+ = "${GNOME2_STABLE}"
REGEX_pn-gtk+3 = "${GNOME_STABLE}"
REGEX_pn-gtk-engines = "${GNOME2_STABLE}"
REGEX_pn-glib-networking = "${GNOME_STABLE}"
REGEX_pn-librsvg = "${GNOME_STABLE}"
REGEX_pn-libsoup-2.4 = "${GNOME_STABLE}"
REGEX_pn-vala = "${GNOME_STABLE}"
REGEX_pn-clutter-1.0 = "${GNOME_STABLE}"
REGEX_pn-clutter-gtk-1.0 = "${GNOME_STABLE}"
REGEX_pn-clutter-gst-3.0 = "${GNOME_STABLE}"
REGEX_pn-cogl-1.0 = "${GNOME_STABLE}"
REGEX_pn-adwaita-icon-theme = "${GNOME_STABLE}"
REGEX_pn-vte = "${GNOME_STABLE}"
REGEX_pn-epiphany = "${GNOME_STABLE}"
REGEX_pn-webkitgtk = "${GNOME_STABLE}"
REGEX_pn-p11-kit = "${GNOME_STABLE}"
REGEX_pn-gnome-desktop3 = "${GNOME_STABLE}"

# not GNOME related but uses same even-minor-release-is-stable logic
REGEX_pn-dbus = "${GNOME_STABLE}"
REGEX_pn-dbus-test = "${GNOME_STABLE}"

# same logic applies to gstreamer releases
REGEX_pn-gstreamer1.0 = "${GNOME_STABLE}"
REGEX_pn-gstreamer1.0-libav = "${GNOME_STABLE}"
REGEX_pn-gstreamer1.0-plugins-bad = "${GNOME_STABLE}"
REGEX_pn-gstreamer1.0-plugins-base = "${GNOME_STABLE}"
REGEX_pn-gstreamer1.0-plugins-good = "${GNOME_STABLE}"
REGEX_pn-gstreamer1.0-plugins-ugly = "${GNOME_STABLE}"
REGEX_pn-gstreamer1.0-rtsp-server = "${GNOME_STABLE}"

# and perl
REGEX_pn-perl = "${GNOME_STABLE}"

# Keep old gcc versions at their major versions
REGEX_pn-gcc-source-4.9.3 = "gcc-(?P<pver>4\.9\.\d+).tar"
REGEX_pn-gcc-source-4.8.4 = "gcc-(?P<pver>4\.8\.\d+).tar"

# these packages are taken from snapshots.debian.org; that source is static and goes stale
# so we check the latest upstream from a directory that does get updated
REGEX_URI_pn-apt = "${DEBIAN_MIRROR}/main/a/apt/"
REGEX_URI_pn-resolvconf = "${DEBIAN_MIRROR}/main/r/resolvconf/"
REGEX_URI_pn-net-tools = "${DEBIAN_MIRROR}/main/n/net-tools/"

# same for packages from launchpad
REGEX_URI_pn-base-passwd = "${DEBIAN_MIRROR}/main/b/base-passwd/"

# for these packages we're mostly interested in tracking debian patches,
# and not in the upstream version where all development has effectively stopped
DEBIAN_PATCH_REGEX = "(?P<pver>((\d+\.*)+)-((\d+\.*)+))\.(diff|debian\.tar)\.(gz|xz)"
REGEX_pn-net-tools = "${DEBIAN_PATCH_REGEX}"
REGEX_pn-apmd = "${DEBIAN_PATCH_REGEX}"
REGEX_pn-blktool = "${DEBIAN_PATCH_REGEX}"
REGEX_pn-mailx = "${DEBIAN_PATCH_REGEX}"
