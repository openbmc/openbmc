SUMMARY = "GStreamer Rust Plugins"
HOMEPAGE = "https://gitlab.freedesktop.org/gstreamer/gst-plugins-rs"

LICENSE = "Apache-2.0 OR MPL-2.0"

SRC_URI += "\
git://gitlab.freedesktop.org/gstreamer/gst-plugins-rs;protocol=https;tag=${PV};nobranch=1;name=default \
"
SRCREV = "6302bea23b53e6461c104f9543df887a63b2d6ec"

LIC_FILES_CHKSUM =  " \
    file://${BPN}-${PV}/LICENSE-MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad \
    file://${BPN}-${PV}/LICENSE-APACHE;md5=1836efb2eb779966696f473ee8540542 \
"

inherit cargo_c cargo-update-recipe-crates
require ${BPN}-crates.inc
require gstreamer1.0-plugins-packaging.inc

# Override split_gstreamer10_packages from gstreamer1.0-plugins-packaging.inc:
# - Skip glibdir/girepository splits since Cargo only produces plugin .so files
#   in gst_libdir, no shared libraries or GIR typelibs
# - Use regex libgst(?:rs)?(.*)\.so$ to strip the "rs" prefix that upstream
#   prepends to some plugin .so names, so package names stay clean
#   (e.g. libgstrsfile.so -> ${PN}-file instead of ${PN}-rsfile)
python split_gstreamer10_packages () {
    gst_libdir = d.expand('${libdir}/gstreamer-1.0')
    postinst = d.getVar('plugin_postinst')

    do_split_packages(d, gst_libdir, r'libgst(?:rs)?(.*)\.so$', d.expand('${PN}-%s'), 'GStreamer 1.0 Rust plugin for %s', postinst=postinst, extra_depends='')

    # GstValidate plugins are installed in a separate subdirectory
    # i.e., 'gstreamer-1.0/validate'
    do_split_packages(d, gst_libdir + '/validate', r'libgst(?:rs)?(.*)\.so$', d.expand('${PN}-%s'), 'GStreamer 1.0 Rust validate plugin for %s', postinst=postinst, extra_depends='')
}

DEPENDS = " \
    glib-2.0 \
    gstreamer1.0 \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-base \
    gst-devtools \
    gtk4 \
"

S = "${UNPACKDIR}"

# Remove the `flavors` and `ffv1` plugins that are not actively maintained in the
# gst-plugins-rs upstream
CARGO_MANIFEST_PATH = "${S}/${CARGO_SRC_DIR}/${BPN}-${PV}/Cargo.toml"
do_configure:prepend() {
    sed -i '/"mux\/flavors"/d' ${CARGO_MANIFEST_PATH}
    sed -i '/"video\/ffv1"/d' ${CARGO_MANIFEST_PATH}
}

# For the purpose of reproducibility, modification of Cargo.lock is disabled
# by default by the cargo_common class using the --frozen flag.
#
# However, the `flavors` and `ffv1` plugins depend on the packages that are not
# published on crates.io and can only be fetched from the git. Also, as mentioned above,
# these plugins are not actively maintained in the gst-plugins-rs upstream, and it is not
# recommended to use them.
#
# So we remove them from the workspace members in the manifest file locally and this will trigger
# a change in the Cargo.lock file.
#
# To allow this exception, disable the --frozen flag so Cargo.lock can be modified,
# but still prevent network access by using the --offline flag in CARGO_BUILD_FLAGS.
CARGO_BUILD_FLAGS:remove = "--frozen"
CARGO_BUILD_FLAGS:append = " --offline"

# cargo_c.bbclass does not pass PACKAGECONFIG_CONFARGS to build and install
# command so without this the whole workspace gets built by default instead of
# specific packages(plugins)
CARGO_BUILD_FLAGS:append = " ${PACKAGECONFIG_CONFARGS}"

# Analytics
PACKAGECONFIG[analytics] = "-p gst-plugin-analytics"
PACKAGECONFIG[burn] = "-p gst-plugin-burn"

# Audio
PACKAGECONFIG[audioparsers] = "-p gst-plugin-audioparsers"
PACKAGECONFIG[audiofx] = "-p gst-plugin-audiofx"
PACKAGECONFIG[claxon] = "-p gst-plugin-claxon"
# FIXME build errors
# PACKAGECONFIG[csound] = "-p gst-plugin-csound"
PACKAGECONFIG[demucs] = "-p gst-plugin-demucs"
PACKAGECONFIG[elevenlabs] = "-p gst-plugin-elevenlabs"
PACKAGECONFIG[lewton] = "-p gst-plugin-lewton"
PACKAGECONFIG[spotify] = "-p gst-plugin-spotify"
PACKAGECONFIG[speechmatics] = "-p gst-plugin-speechmatics"
# FIXME build errors
# PACKAGECONFIG[whisper] = "-p gst-plugin-whisper"

# Generic
PACKAGECONFIG[file] = "-p gst-plugin-file"
PACKAGECONFIG[originalbuffer] = "-p gst-plugin-originalbuffer"
PACKAGECONFIG[gopbuffer] = "-p gst-plugin-gopbuffer"
PACKAGECONFIG[sodium] = "-p gst-plugin-sodium"
PACKAGECONFIG[threadshare] = "-p gst-plugin-threadshare"
PACKAGECONFIG[inter] = "-p gst-plugin-inter"
PACKAGECONFIG[streamgrouper] = "-p gst-plugin-streamgrouper"

# Mux
# PACKAGECONFIG[flavors] = "-p gst-plugin-flavors"
PACKAGECONFIG[isobmff] = "-p gst-plugin-isobmff"

# Net
PACKAGECONFIG[aws] = "-p gst-plugin-aws"
PACKAGECONFIG[deepgram] = "-p gst-plugin-deepgram"
PACKAGECONFIG[hlsmultivariantsink] = "-p gst-plugin-hlsmultivariantsink"
PACKAGECONFIG[hlssink3] = "-p gst-plugin-hlssink3"
PACKAGECONFIG[icecast] = "-p gst-plugin-icecast"
PACKAGECONFIG[mpegtslive] = "-p gst-plugin-mpegtslive"
PACKAGECONFIG[ndi] = "-p gst-plugin-ndi"
PACKAGECONFIG[onvif] = "-p gst-plugin-onvif"
PACKAGECONFIG[raptorq] = "-p gst-plugin-raptorq"
PACKAGECONFIG[reqwest] = "-p gst-plugin-reqwest"
PACKAGECONFIG[rtsp] = "-p gst-plugin-rtsp"
PACKAGECONFIG[rtp] = "-p gst-plugin-rtp"
PACKAGECONFIG[webrtc] = "-p gst-plugin-webrtc"
PACKAGECONFIG[webrtc-signalling] = "-p gst-plugin-webrtc-signalling"
PACKAGECONFIG[webrtc-signalling-protocol] = "-p gst-plugin-webrtc-signalling-protocol"
PACKAGECONFIG[webrtchttp] = "-p gst-plugin-webrtchttp"
PACKAGECONFIG[quinn] = "-p gst-plugin-quinn"

# Text
PACKAGECONFIG[textaccumulate] = "-p gst-plugin-textaccumulate"
PACKAGECONFIG[textahead] = "-p gst-plugin-textahead"
PACKAGECONFIG[json] = "-p gst-plugin-json"
PACKAGECONFIG[regex] = "-p gst-plugin-regex"
PACKAGECONFIG[textwrap] = "-p gst-plugin-textwrap"

# Utils
PACKAGECONFIG[fallbackswitch] = "-p gst-plugin-fallbackswitch"
PACKAGECONFIG[livesync] = "-p gst-plugin-livesync"
PACKAGECONFIG[debugseimetainserter] = "-p gst-plugin-debugseimetainserter"
PACKAGECONFIG[togglerecord] = "-p gst-plugin-togglerecord"
PACKAGECONFIG[tracers] = "-p gst-plugin-tracers"
PACKAGECONFIG[uriplaylistbin] = "-p gst-plugin-uriplaylistbin"
PACKAGECONFIG[validate] = "-p gst-plugin-validate"

# Video
PACKAGECONFIG[cdg] = "-p gst-plugin-cdg"
PACKAGECONFIG[closedcaption] = "-p gst-plugin-closedcaption"
# need dav1d recipe meta-openembedded/meta-multimedia
PACKAGECONFIG[dav1d] = "-p gst-plugin-dav1d,,dav1d, dav1d"
PACKAGECONFIG[ffv1] = "-p gst-plugin-ffv1"
PACKAGECONFIG[gif] = "-p gst-plugin-gif"
PACKAGECONFIG[gtk4] = "-p gst-plugin-gtk4"
PACKAGECONFIG[hsv] = "-p gst-plugin-hsv"
PACKAGECONFIG[png] = "-p gst-plugin-png"
PACKAGECONFIG[rav1e] = "-p gst-plugin-rav1e"
# FIXME failing to download skia from
# https://codeload.github.com/rust-skia/skia/tar.gz/m145-0.92.0
# PACKAGECONFIG[skia] = "-p gst-plugin-skia"
PACKAGECONFIG[videofx] = "-p gst-plugin-videofx"
# TODO no vvdec recipe
# PACKAGECONFIG[vvdec] = "-p gst-plugin-vvdec"
PACKAGECONFIG[webp] = "-p gst-plugin-webp"

# Default members
PACKAGECONFIG ??= " \
    audiofx \
    closedcaption \
    file \
    fallbackswitch \
    tracers \
    threadshare \
    rtp \
    inter \
    isobmff \
    hlssink3 \
    mpegtslive \
    reqwest \
    rtsp \
    webrtc \
    webrtc-signalling \
    videofx \
    webp \
"

# The 'ring' crate (pulled in transitively by plugins such as reqwest,
# webrtc, quinn, etc.) requires SSE2 on 32-bit x86.
#
# rust-target-config.bbclass only enables sse2 (or a superset) in the
# generated rust target if TARGET_CC_ARCH carries an explicit -msse*
# flag; -march= values are not parsed.
#
# Skip the recipe for 32-bit x86 tunes that do not guarantee SSE2
# (x86, i586, i586-nlp, i686, c3). SSE2-capable tunes (core2-32,
# corei7-32, all x86-64 variants) are unaffected.
python () {
    if (d.getVar('TRANSLATED_TARGET_ARCH') or '') not in ('i386', 'i486', 'i586', 'i686'):
        return
    cc_flags = (d.getVar('TARGET_CC_ARCH') or '').split()
    sse2_flags = ('-msse2', '-msse3', '-mssse3', '-msse4.1', '-msse4.2', '-msse4a')
    if not any(f in cc_flags for f in sse2_flags):
        raise bb.parse.SkipRecipe(
            "Depends transitively on the 'ring' crate which requires SSE2, "
            "not guaranteed by this tune (TUNE_FEATURES='%s'). Use an "
            "SSE2-capable x86 tune such as core2-32 or corei7-32."
            % (d.getVar('TUNE_FEATURES') or ''))
}
