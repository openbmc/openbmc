require flite.inc

PR = "r1"

EXTRA_OECONF = "--with-audio=alsa --enable-shared"

SRC_URI = "http://www.speech.cs.cmu.edu/flite/packed/flite-${PV}/flite-${PV}-release.tar.gz \
           file://flite-1.3-alsa_support-1.2.diff \
           file://flite-alsa-1.3-configure-with-audio.patch"

SRC_URI[md5sum] = "ae0aca1cb7b4801f4372f3a75a9e52b5"
SRC_URI[sha256sum] = "922225f7001e57a0fbace8833b0a48790a68f6c7b491f2d47c78ad537ab78a8b"

# Looks like caused by flite-1.3-alsa_support-1.2.diff
# flite-alsa-1.3: No GNU_HASH in the elf binary: '/tmp/work/armv5te-oe-linux-gnueabi/flite-alsa/1.3-r1/packages-split/libflite-alsa-vox16/usr/lib/libflite_cmu_us_kal16.so.1.3' [ldflags]
# flite-alsa-1.3: No GNU_HASH in the elf binary: '/tmp/work/armv5te-oe-linux-gnueabi/flite-alsa/1.3-r1/packages-split/libflite-alsa-vox8/usr/lib/libflite_cmu_us_kal.so.1.3' [ldflags]
# flite-alsa-1.3: No GNU_HASH in the elf binary: '/tmp/work/core2-64-oe-linux/flite-alsa/1.3-r1/packages-split/libflite-alsa-vox16/usr/lib/libflite_cmu_us_kal16.so.1.3' [ldflags]
# flite-alsa-1.3: No GNU_HASH in the elf binary: '/tmp/work/core2-64-oe-linux/flite-alsa/1.3-r1/packages-split/libflite-alsa-vox8/usr/lib/libflite_cmu_us_kal.so.1.3' [ldflags]
# flite-alsa-1.3: No GNU_HASH in the elf binary: '/tmp/work/i586-oe-linux/flite-alsa/1.3-r1/packages-split/libflite-alsa-vox16/usr/lib/libflite_cmu_us_kal16.so.1.3' [ldflags]
# flite-alsa-1.3: No GNU_HASH in the elf binary: '/tmp/work/i586-oe-linux/flite-alsa/1.3-r1/packages-split/libflite-alsa-vox8/usr/lib/libflite_cmu_us_kal.so.1.3' [ldflags]
PNBLACKLIST[flite-alsa] ?= "OLD: wasn't updated in over 6 years, only navit was RSUGGESTing it and doesn't respect LDFLAGS - the recipe will be removed on 2017-09-01 unless the issue is fixed"
