DESCRIPTION = "Xen hypervisor"
HOMEPAGE = "http://xen.org"
LICENSE = "GPLv2"
SECTION = "console/tools"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbb4b1bdc2c3b6743da3c39d03249095"

COMPATIBLE_HOST = '(x86_64.*).*-linux|aarch64.*-linux'

inherit autotools-brokensep gettext setuptools update-rc.d systemd deploy

require xen-arch.inc

PACKAGECONFIG ??= " \
    sdl \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
    ${@bb.utils.contains('XEN_TARGET_ARCH', 'x86_64', 'hvm', '', d)} \
    "

FLASK_POLICY_FILE ?= "xenpolicy-${PV}"

PACKAGECONFIG[sdl] = "--enable-sdl,--disable-sdl,virtual/libsdl,"
PACKAGECONFIG[xsm] = "--enable-xsmpolicy,--disable-xsmpolicy,checkpolicy-native,"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd,"
PACKAGECONFIG[hvm] = "--with-system-seabios="${STAGING_DIR_HOST}/usr/share/firmware/bios.bin",--disable-seabios,seabios ipxe vgabios,"

DEPENDS = " \
    bison-native \
    dev86-native \
    flex-native \
    file-native \
    iasl-native \
    util-linux-native \
    xz-native \
    bridge-utils \
    curl \
    dtc \
    gettext \
    glib-2.0 \
    gnutls \
    iproute2 \
    libnl \
    ncurses \
    openssl \
    pciutils \
    pixman \
    procps \
    python \
    libaio \
    lzo \
    util-linux \
    xz \
    yajl \
    zlib \
    gnu-efi \
    "

# inherit setuptools adds python to RDEPENDS, override it
RDEPENDS_${PN} = ""

RDEPENDS_${PN}-base = "\
    bash perl xz \
    ${PN}-blktap \
    ${PN}-console \
    ${PN}-libblktapctl \
    ${PN}-libxenguest \
    ${PN}-libxenlight \
    ${PN}-libxenvchan \
    ${PN}-libxenctrl \
    ${PN}-libxlutil \
    ${PN}-libvhd \
    ${PN}-libxenstat \
    ${PN}-libxenstore \
    ${PN}-libfsimage \
    ${PN}-fsimage \
    ${PN}-scripts-block \
    ${PN}-scripts-network \
    ${PN}-xen-watchdog \
    ${PN}-xencommons \
    ${PN}-xendomains \
    ${PN}-xenstore \
    ${PN}-xenstored \
    ${PN}-xl \
    "

RDEPENDS_${PN}-dev = ""

RRECOMMENDS_${PN}-base = " \
    ${PN}-libblktap \
    ${PN}-flask \
    ${PN}-hvmloader \
    ${PN}-xenpaging \
    "

RDEPENDS_${PN}-devd = " \
    ${PN}-xl \
    "

RDEPENDS_${PN}-fsimage = " \
    libext2fs \
    "

RDEPENDS_${PN}-misc = " \
    perl \
    python \
    "

RDEPENDS_${PN}-remus = " \
    bash \
    python \
    "

RDEPENDS_${PN}-scripts-block = "\
    bash \
    ${PN}-scripts-common \
    ${PN}-volatiles \
    "

RDEPENDS_${PN}-scripts-common = "bash"

RDEPENDS_${PN}-scripts-network = "\
    bash \
    bridge-utils \
    ${PN}-scripts-common \
    ${PN}-volatiles \
    "

RDEPENDS_${PN}-xencommons = "\
    bash \
    ${PN}-console \
    ${PN}-xenstore \
    ${PN}-xenstored \
    ${PN}-xl \
    ${PN}-scripts-common \
    "

RDEPENDS_${PN}-xendomains = "\
    bash \
    ${PN}-console \
    ${PN}-scripts-block \
    ${PN}-scripts-common \
    ${PN}-xenstored \
    "

RDEPENDS_${PN}-xl = "libgcc"

RDEPENDS_${PN}-xentrace = "python"

RDEPENDS_${PN}-xen-watchdog = "bash"

PACKAGES = "\
    ${PN}-base \
    ${PN}-blktap \
    ${PN}-console \
    ${PN}-cpuid \
    ${PN}-dbg \
    ${PN}-dev \
    ${PN}-devd \
    ${PN}-doc \
    ${PN}-efi \
    ${PN}-flask \
    ${PN}-flask-tools \
    ${PN}-fsimage \
    ${PN}-gdbsx \
    ${PN}-hvmloader \
    ${PN}-hypervisor \
    ${PN}-init-xenstore-dom \
    ${PN}-kdd \
    ${PN}-libblktap \
    ${PN}-libblktapctl \
    ${PN}-libblktapctl-dev \
    ${PN}-libblktap-dev \
    ${PN}-libfsimage \
    ${PN}-libfsimage-dev \
    ${PN}-libvhd \
    ${PN}-libvhd-dev \
    ${PN}-libxencall \
    ${PN}-libxencall-dev \
    ${PN}-libxenctrl \
    ${PN}-libxenctrl-dev \
    ${PN}-libxenevtchn \
    ${PN}-libxenevtchn-dev \
    ${PN}-libxenforeignmemory \
    ${PN}-libxenforeignmemory-dev \
    ${PN}-libxengnttab \
    ${PN}-libxengnttab-dev \
    ${PN}-libxenguest \
    ${PN}-libxenguest-dev \
    ${PN}-libxenlight \
    ${PN}-libxenlight-dev \
    ${PN}-libxenstat \
    ${PN}-libxenstat-dev \
    ${PN}-libxenstore \
    ${PN}-libxenstore-dev \
    ${PN}-libxentoollog \
    ${PN}-libxentoollog-dev \
    ${PN}-libxenvchan \
    ${PN}-libxenvchan-dev \
    ${PN}-libxlutil \
    ${PN}-libxlutil-dev \
    ${PN}-livepatch \
    ${PN}-misc \
    ${PN}-pygrub \
    ${PN}-python \
    ${PN}-qemu \
    ${PN}-remus \
    ${PN}-scripts-block \
    ${PN}-scripts-common \
    ${PN}-scripts-network \
    ${PN}-staticdev \
    ${PN}-volatiles \
    ${PN}-xcutils \
    ${PN}-xencommons \
    ${PN}-xend \
    ${PN}-xend-examples \
    ${PN}-xendomains \
    ${PN}-xenmon \
    ${PN}-xenpaging \
    ${PN}-xenpmd \
    ${PN}-xenstat \
    ${PN}-xenstore \
    ${PN}-xenstored \
    ${PN}-xentrace \
    ${PN}-xen-watchdog \
    ${PN}-xl \
    ${PN}-xl-examples \
    ${PN}-xm \
    ${PN}-xm-examples \
    "

FILES_${PN}-dbg += "\
    ${libdir}/.debug \
    ${libdir}/debug/* \
    ${libdir}/xen/bin/.debug \
    ${libdir}/python2.7/site-packages/.debug \
    ${libdir}/python2.7/site-packages/xen/lowlevel/.debug \
    ${libdir}/fs/xfs/.debug \
    ${libdir}/fs/ufs/.debug \
    ${libdir}/fs/ext2fs-lib/.debug \
    ${libdir}/fs/fat/.debug \
    ${libdir}/fs/zfs/.debug \
    ${libdir}/fs/reiserfs/.debug \
    ${libdir}/fs/iso9660/.debug \
    ${libdir}/fs/*/.debug \
    ${sbindir}/.debug \
    ${libdir}exec/.debug \
    ${libdir}/xen/libexec/.debug \
    ${bindir}/.debug \
    ${libdir}/python2.7/dist-packages/.debug \
    ${libdir}/python2.7/dist-packages/xen/lowlevel/.debug \
    "

FILES_${PN}-dev = "\
    ${includedir} \
    "

FILES_${PN}-doc = "\
    ${sysconfdir}/xen/README \
    ${sysconfdir}/xen/README.incompatibilities \
    ${datadir}/doc \
    ${datadir}/man \
    "

FILES_${PN}-staticdev += "\
    ${libdir}/libblktapctl.a \
    ${libdir}/libxenguest.a \
    ${libdir}/libxenlight.a \
    ${libdir}/libxenvchan.a \
    ${libdir}/libxenctrl.a \
    ${libdir}/libxlutil.a \
    ${libdir}/libvhd.a \
    ${libdir}/libxenstat.a \
    ${libdir}/libxenstore.a \
    ${libdir}/libblktap.a \
    "

FILES_${PN}-libblktapctl = "${libdir}/libblktapctl.so.*"
FILES_${PN}-libblktapctl-dev = "${libdir}/libblktapctl.so"

FILES_${PN}-libxencall = "${libdir}/libxencall.so.*"
FILES_${PN}-libxencall-dev = "${libdir}/libxencall.so"

FILES_${PN}-libxenctrl = "${libdir}/libxenctrl.so.*"
FILES_${PN}-libxenctrl-dev = "${libdir}/libxenctrl.so"

FILES_${PN}-libxenevtchn = "${libdir}/libxenevtchn.so.*"
FILES_${PN}-libxenevtchn-dev = "${libdir}/libxenevtchn.so"

FILES_${PN}-libxenforeignmemory = "${libdir}/libxenforeignmemory.so.*"
FILES_${PN}-libxenforeignmemory-dev = "${libdir}/libxenforeignmemory.so"

FILES_${PN}-libxengnttab = "${libdir}/libxengnttab.so.*"
FILES_${PN}-libxengnttab-dev = "${libdir}/libxengnttab.so"

FILES_${PN}-libxenguest = "${libdir}/libxenguest.so.*"
FILES_${PN}-libxenguest-dev = "${libdir}/libxenguest.so"

FILES_${PN}-libxenlight = "${libdir}/libxenlight.so.*"
FILES_${PN}-libxenlight-dev = " \
    ${libdir}/libxenlight.so \
    ${datadir}/pkgconfig/xenlight.pc \
    "

FILES_${PN}-libxenstat = "${libdir}/libxenstat.so.*"
FILES_${PN}-libxenstat-dev = "${libdir}/libxenstat.so"

FILES_${PN}-libxenstore = "${libdir}/libxenstore.so.*"
FILES_${PN}-libxenstore-dev = "${libdir}/libxenstore.so"

FILES_${PN}-libxentoollog = "${libdir}/libxentoollog.so.*"
FILES_${PN}-libxentoollog-dev = "${libdir}/libxentoollog.so"

FILES_${PN}-libxenvchan = "${libdir}/libxenvchan.so.*"
FILES_${PN}-libxenvchan-dev = "${libdir}/libxenvchan.so"

FILES_${PN}-libxlutil = "${libdir}/libxlutil.so.*"
FILES_${PN}-libxlutil-dev = " \
    ${libdir}/libxlutil.so \
    ${datadir}/pkgconfig/xlutil.pc \
    "
FILES_${PN}-libvhd = "${libdir}/libvhd.so.*"
FILES_${PN}-libvhd-dev = "${libdir}/libvhd.so"

FILES_${PN}-libblktap = "${libdir}/libblktap.so.*"
FILES_${PN}-libblktap-dev = "${libdir}/libblktap.so"

FILES_${PN}-libfsimage = "${libdir}/libfsimage.so.*"
FILES_${PN}-libfsimage-dev = "${libdir}/libfsimage.so"

FILES_${PN}-fsimage = "${libdir}/fs/*/*fsimage.so"

FILES_${PN}-hypervisor = "\
    /boot/xen-* \
    /boot/xen \
    /boot/xen-*.gz \
    /boot/xen.gz \
    /boot/xen-syms-* \
    "

FILES_${PN}-init-xenstore-dom = "${libdir}/xen/bin/init-xenstore-domain"

FILES_${PN}-efi = "\
    ${exec_prefix}/lib64 \
    ${exec_prefix}/lib64/xen* \
    "

FILES_${PN}-base = "\
    ${sysconfdir}/xen/auto \
    ${sysconfdir}/xen/cpupool \
    ${localstatedir}/xen/dump \
    "

FILES_${PN}-blktap = "\
    ${sbindir}/blktapctrl \
    ${sbindir}/img2qcow \
    ${sbindir}/lock-util \
    ${sbindir}/qcow2raw \
    ${sbindir}/qcow-create \
    ${sbindir}/tap-ctl \
    ${sbindir}/tapdisk \
    ${sbindir}/tapdisk2 \
    ${sbindir}/tapdisk-client \
    ${sbindir}/tapdisk-diff \
    ${sbindir}/tapdisk-stream \
    ${sbindir}/td-util \
    ${sbindir}/vhd-update \
    ${sbindir}/vhd-util \
    "

FILES_${PN}-console = "\
    ${libdir}/xen/bin/xenconsole \
    ${sbindir}/xenconsoled \
    "

FILES_${PN}-cpuid = "\
    ${bindir}/xen-cpuid \
    "

FILES_${PN}-devd = "\
    ${sysconfdir}/init.d/xendriverdomain \
    "

FILES_${PN}-flask = "\
    /boot/${FLASK_POLICY_FILE} \
    "

FILES_${PN}-flask-tools = "\
    ${sbindir}/flask-get-bool \
    ${sbindir}/flask-getenforce \
    ${sbindir}/flask-label-pci \
    ${sbindir}/flask-loadpolicy \
    ${sbindir}/flask-set-bool \
    ${sbindir}/flask-setenforce \
    "

FILES_${PN}-gdbsx = "\
    ${sbindir}/gdbsx \
    "

INSANE_SKIP_${PN}-hvmloader = "arch"
FILES_${PN}-hvmloader = "\
    ${libdir}/xen/boot/hvmloader \
    "

FILES_${PN}-kdd = "\
    ${sbindir}/kdd \
    "

FILES_${PN}-livepatch += " \
    ${sbindir}/xen-livepatch \
    "

FILES_${PN}-misc = "\
    ${bindir}/xencons \
    ${bindir}/xencov_split \
    ${bindir}/xen-detect \
    ${libdir}/xen/bin/xenpvnetboot \
    ${sbindir}/gtracestat \
    ${sbindir}/gtraceview \
    ${sbindir}/xen-bugtool \
    ${sbindir}/xencov \
    ${sbindir}/xenperf \
    ${sbindir}/xenpm \
    ${sbindir}/xsview \
    ${sbindir}/xen-tmem-list-parse \
    ${sbindir}/xen-python-path \
    ${sbindir}/xen-ringwatch \
    ${sbindir}/xen-hptool \
    ${sbindir}/xen-hvmcrash \
    ${sbindir}/xen-hvmctx \
    ${sbindir}/xenlockprof \
    ${sbindir}/xen-lowmemd \
    ${sbindir}/xen-mfndump \
    ${libdir}/xen/bin/verify-stream-v2 \
    ${libdir}/xen/bin/convert-legacy-stream \
    "

FILES_${PN}-pygrub = "\
    ${bindir}/pygrub \
    ${libdir}/xen/bin/pygrub \
    "

FILES_${PN}-python = "\
    ${libdir}/python2.7 \
    "

INSANE_SKIP_${PN} = "already-stripped"
INSANE_SKIP_${PN}-qemu = "arch"
FILES_${PN}-qemu = " \
    ${datadir}/xen/qemu \
    ${libdir}/xen/bin/qemu-system-i386 \
    ${libdir}/xen/bin/qemu-system-x86_64 \
    ${libdir}/xen/bin/qemu-img \
    ${libdir}/xen/bin/qemu-nbd \
    ${libdir}/xen/bin/qemu-ga \
    ${libdir}/xen/bin/qemu-io \
    ${libdir}/xen/bin/qemu-dm \
    ${libdir}/xen/bin/virtfs-proxy-helper \
    /usr/libexec/qemu-bridge-helper \
    ${libdir}/xen/libexec/qemu-bridge-helper \
    /usr/etc/qemu \
    /usr/etc/qemu/target-x86_64.conf \
    ${libdir}/xen/etc/qemu/target-x86_64.conf \
    ${datadir}/qemu-xen \
    ${datadir}/qemu-xen/qemu \
    ${datadir}/qemu-xen/qemu/bamboo.dtb \
    ${datadir}/qemu-xen/qemu/pxe-pcnet.rom \
    ${datadir}/qemu-xen/qemu/vgabios-vmware.bin \
    ${datadir}/qemu-xen/qemu/pxe-eepro100.rom \
    ${datadir}/qemu-xen/qemu/pxe-e1000.rom \
    ${datadir}/qemu-xen/qemu/openbios-ppc \
    ${datadir}/qemu-xen/qemu/multiboot.bin \
    ${datadir}/qemu-xen/qemu/vgabios-cirrus.bin \
    ${datadir}/qemu-xen/qemu/bios.bin \
    ${datadir}/qemu-xen/qemu/vgabios-stdvga.bin \
    ${datadir}/qemu-xen/qemu/palcode-clipper \
    ${datadir}/qemu-xen/qemu/pxe-ne2k_pci.rom \
    ${datadir}/qemu-xen/qemu/spapr-rtas.bin \
    ${datadir}/qemu-xen/qemu/slof.bin \
    ${datadir}/qemu-xen/qemu/vgabios-qxl.bin \
    ${datadir}/qemu-xen/qemu/pxe-rtl8139.rom \
    ${datadir}/qemu-xen/qemu/openbios-sparc64 \
    ${datadir}/qemu-xen/qemu/pxe-virtio.rom \
    ${datadir}/qemu-xen/qemu/kvmvapic.bin \
    ${datadir}/qemu-xen/qemu/openbios-sparc32 \
    ${datadir}/qemu-xen/qemu/petalogix-s3adsp1800.dtb \
    ${datadir}/qemu-xen/qemu/sgabios.bin \
    ${datadir}/qemu-xen/qemu/linuxboot.bin \
    ${datadir}/qemu-xen/qemu/qemu-icon.bmp \
    ${datadir}/qemu-xen/qemu/ppc_rom.bin \
    ${datadir}/qemu-xen/qemu/vgabios.bin \
    ${datadir}/qemu-xen/qemu/s390-zipl.rom \
    ${datadir}/qemu-xen/qemu/petalogix-ml605.dtb \
    ${datadir}/qemu-xen/qemu/keymaps \
    ${datadir}/qemu-xen/qemu/keymaps/common \
    ${datadir}/qemu-xen/qemu/keymaps/th \
    ${datadir}/qemu-xen/qemu/keymaps/is \
    ${datadir}/qemu-xen/qemu/keymaps/en-gb \
    ${datadir}/qemu-xen/qemu/keymaps/ar \
    ${datadir}/qemu-xen/qemu/keymaps/fr-be \
    ${datadir}/qemu-xen/qemu/keymaps/ru \
    ${datadir}/qemu-xen/qemu/keymaps/hu \
    ${datadir}/qemu-xen/qemu/keymaps/de-ch \
    ${datadir}/qemu-xen/qemu/keymaps/no \
    ${datadir}/qemu-xen/qemu/keymaps/fr \
    ${datadir}/qemu-xen/qemu/keymaps/pl \
    ${datadir}/qemu-xen/qemu/keymaps/fr-ca \
    ${datadir}/qemu-xen/qemu/keymaps/de \
    ${datadir}/qemu-xen/qemu/keymaps/fr-ch \
    ${datadir}/qemu-xen/qemu/keymaps/bepo \
    ${datadir}/qemu-xen/qemu/keymaps/lv \
    ${datadir}/qemu-xen/qemu/keymaps/ja \
    ${datadir}/qemu-xen/qemu/keymaps/da \
    ${datadir}/qemu-xen/qemu/keymaps/lt \
    ${datadir}/qemu-xen/qemu/keymaps/hr \
    ${datadir}/qemu-xen/qemu/keymaps/es \
    ${datadir}/qemu-xen/qemu/keymaps/modifiers \
    ${datadir}/qemu-xen/qemu/keymaps/sl \
    ${datadir}/qemu-xen/qemu/keymaps/it \
    ${datadir}/qemu-xen/qemu/keymaps/nl \
    ${datadir}/qemu-xen/qemu/keymaps/fo \
    ${datadir}/qemu-xen/qemu/keymaps/mk \
    ${datadir}/qemu-xen/qemu/keymaps/pt-br \
    ${datadir}/qemu-xen/qemu/keymaps/tr \
    ${datadir}/qemu-xen/qemu/keymaps/sv \
    ${datadir}/qemu-xen/qemu/keymaps/fi \
    ${datadir}/qemu-xen/qemu/keymaps/en-us \
    ${datadir}/qemu-xen/qemu/keymaps/et \
    ${datadir}/qemu-xen/qemu/keymaps/nl-be \
    ${datadir}/qemu-xen/qemu/keymaps/pt \
    ${bindir}/qemu-nbd-xen \
    ${bindir}/qemu-img-xen \
    "

FILES_${PN}-remus = "\
    ${bindir}/remus \
    ${sysconfdir}/xen/scripts/remus-netbuf-setup \
    "

FILES_${PN}-scripts-network = " \
    ${sysconfdir}/xen/scripts/colo-proxy-setup \
    ${sysconfdir}/xen/scripts/network-bridge \
    ${sysconfdir}/xen/scripts/network-nat \
    ${sysconfdir}/xen/scripts/network-route \
    ${sysconfdir}/xen/scripts/qemu-ifup \
    ${sysconfdir}/xen/scripts/vif2 \
    ${sysconfdir}/xen/scripts/vif-bridge \
    ${sysconfdir}/xen/scripts/vif-common.sh \
    ${sysconfdir}/xen/scripts/vif-nat \
    ${sysconfdir}/xen/scripts/vif-openvswitch \
    ${sysconfdir}/xen/scripts/vif-route \
    ${sysconfdir}/xen/scripts/vif-setup \
    "

FILES_${PN}-scripts-block = " \
    ${sysconfdir}/xen/scripts/blktap \
    ${sysconfdir}/xen/scripts/block \
    ${sysconfdir}/xen/scripts/block-common.sh \
    ${sysconfdir}/xen/scripts/block-dummy \
    ${sysconfdir}/xen/scripts/block-enbd \
    ${sysconfdir}/xen/scripts/block-iscsi \
    ${sysconfdir}/xen/scripts/block-nbd \
    ${sysconfdir}/xen/scripts/block-drbd-probe \
    ${sysconfdir}/xen/scripts/block-tap \
    ${sysconfdir}/xen/scripts/vscsi \
    "

FILES_${PN}-scripts-common = " \
    ${sysconfdir}/xen/scripts/external-device-migrate \
    ${sysconfdir}/xen/scripts/hotplugpath.sh \
    ${sysconfdir}/xen/scripts/locking.sh \
    ${sysconfdir}/xen/scripts/logging.sh \
    ${sysconfdir}/xen/scripts/xen-hotplug-cleanup \
    ${sysconfdir}/xen/scripts/xen-hotplug-common.sh \
    ${sysconfdir}/xen/scripts/xen-network-common.sh \
    ${sysconfdir}/xen/scripts/xen-script-common.sh \
    "

FILES_${PN}-volatiles = "\
    ${sysconfdir}/default/volatiles/99_xen \
    ${sysconfdir}/tmpfiles.d/xen.conf \
    "

FILES_${PN}-xcutils = "\
    ${libdir}/xen/bin/lsevtchn \
    ${libdir}/xen/bin/readnotes \
    ${libdir}/xen/bin/xc_restore \
    ${libdir}/xen/bin/xc_save \
    "

FILES_${PN}-xend-examples = "\
    ${sysconfdir}/xen/xend-config.sxp \
    ${sysconfdir}/xen/xend-pci-permissive.sxp \
    ${sysconfdir}/xen/xend-pci-quirks.sxp \
    "

FILES_${PN}-xenpaging = "\
    ${libdir}/xen/bin/xenpaging \
    ${localstatedir}/lib/xen/xenpaging \
    "

FILES_${PN}-xenpmd = "\
    ${sbindir}/xenpmd \
    "

FILES_${PN}-xenstat = "\
    ${sbindir}/xentop \
    "

FILES_${PN}-xenstore = "\
    ${bindir}/xenstore \
    ${bindir}/xenstore-chmod \
    ${bindir}/xenstore-control \
    ${bindir}/xenstore-exists \
    ${bindir}/xenstore-list \
    ${bindir}/xenstore-ls \
    ${bindir}/xenstore-read \
    ${bindir}/xenstore-rm \
    ${bindir}/xenstore-watch \
    ${bindir}/xenstore-write \
    "

FILES_${PN}-xenstored = "\
    ${sbindir}/xenstored \
    ${localstatedir}/lib/xenstored \
    "

FILES_${PN}-xentrace = "\
    ${bindir}/xentrace \
    ${bindir}/xentrace_format \
    ${bindir}/xentrace_setsize \
    ${libdir}/xen/bin/xenctx \
    ${bindir}/xenalyze \
    ${sbindir}/xentrace \
    ${sbindir}/xentrace_setsize \
    "

FILES_${PN}-xen-watchdog = "\
    ${sbindir}/xenwatchdogd \
    ${sysconfdir}/init.d/xen-watchdog \
    ${systemd_unitdir}/system/xen-watchdog.service \
    "

FILES_${PN}-xl = "\
    ${sysconfdir}/bash_completion.d/xl.sh \
    ${sysconfdir}/xen/xl.conf \
    ${libdir}/xen/bin/libxl-save-helper \
    ${sbindir}/xl \
    ${libdir}/xen/bin/xen-init-dom0 \
    "

FILES_${PN}-xl-examples = "\
    ${sysconfdir}/xen/xlexample.hvm \
    ${sysconfdir}/xen/xlexample.pvlinux \
    "

FILES_${PN}-xm-examples = "\
    ${sysconfdir}/xen/xmexample1 \
    ${sysconfdir}/xen/xmexample2 \
    ${sysconfdir}/xen/xmexample3 \
    ${sysconfdir}/xen/xmexample.hvm \
    ${sysconfdir}/xen/xmexample.hvm-stubdom \
    ${sysconfdir}/xen/xmexample.nbd \
    ${sysconfdir}/xen/xmexample.pv-grub \
    ${sysconfdir}/xen/xmexample.vti \
    "

FILES_${PN}-xenmon = "\
    ${sbindir}/xenbaked \
    ${sbindir}/xentrace_setmask \
    ${sbindir}/xenmon.py \
    "

FILES_${PN}-xm = "\
    ${sysconfdir}/xen/xm-config.xml \
    ${datadir}/xen/create.dtd \
    ${sbindir}/xm \
    "

FILES_${PN}-xencommons += "\
    ${sysconfdir}/default/xencommons \
    ${sysconfdir}/init.d/xencommons \
    ${sysconfdir}/xen/scripts/launch-xenstore \
    ${systemd_unitdir}/modules-load.d/xen.conf \
    ${systemd_unitdir}/system/proc-xen.mount \
    ${systemd_unitdir}/system/xen-qemu-dom0-disk-backend.service \
    ${systemd_unitdir}/system/xenconsoled.service \
    ${systemd_unitdir}/system/xen-init-dom0.service \
    ${systemd_unitdir}/system/xenstored.service \
    ${systemd_unitdir}/system/xenstored.socket \
    ${systemd_unitdir}/system/xenstored_ro.socket \
    ${systemd_unitdir}/system/var-lib-xenstored.mount \
    "

FILES_${PN}-xend += " \
    ${sysconfdir}/init.d/xend \
    ${sbindir}/xend \
    "

FILES_${PN}-xendomains += "\
    ${libdir}/xen/bin/xendomains \
    ${sysconfdir}/default/xendomains \
    ${sysconfdir}/init.d/xendomains \
    ${sysconfdir}/sysconfig/xendomains \
    ${systemd_unitdir}/system/xendomains.service \
    "

# configure init.d scripts
INITSCRIPT_PACKAGES = "${PN}-xend ${PN}-xencommons ${PN}-xen-watchdog ${PN}-xendomains ${PN}-devd"
INITSCRIPT_NAME_${PN}-xencommons = "xencommons"
INITSCRIPT_PARAMS_${PN}-xencommons = "defaults 80"
INITSCRIPT_NAME_${PN}-xen-watchdog = "xen-watchdog"
INITSCRIPT_PARAMS_${PN}-xen-watchdog = "defaults 81"
INITSCRIPT_NAME_${PN}-xend = "xend"
INITSCRIPT_PARAMS_${PN}-xend = "defaults 82"
INITSCRIPT_NAME_${PN}-xendomains = "xendomains"
INITSCRIPT_PARAMS_${PN}-xendomains = "defaults 83"
INITSCRIPT_NAME_${PN}-devd = "xendriverdomain"
INITSCRIPT_PARAMS_${PN}-devd = "defaults 82"

# systemd packages
SYSTEMD_PACKAGES = "${PN}-xen-watchdog ${PN}-xencommons ${PN}-xendomains"
SYSTEMD_SERVICE_${PN}-watchdog = "xen-watchdog.service"
SYSTEMD_SERVICE_${PN}-xencommons = " \
    proc-xen.mount \
    var-lib-xenstored.mount \
    xen-qemu-dom0-disk-backend.service \
    xenconsoled.service \
    xen-init-dom0.service \
    xenstored.service \
    xenstored.socket \
    xenstored_ro.socket \
    "
SYSTEMD_SERVICE_${PN}-xendomains = "xendomains.service"

#### REQUIRED ENVIRONMENT VARIABLES ####
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

# specify xen hypervisor to build/target
export XEN_TARGET_ARCH = "${@map_xen_arch(d.getVar('TARGET_ARCH', True), d)}"
export XEN_COMPILE_ARCH = "${@map_xen_arch(d.getVar('BUILD_ARCH', True), d)}"

python () {
    if d.getVar('XEN_TARGET_ARCH', True) == 'INVALID':
        raise bb.parse.SkipPackage('Cannot map `%s` to a xen architecture' % d.getVar('TARGET_ARCH', True))
}

# Yocto appends ${PN} to libexecdir by default and Xen appends 'xen' as well
# the result is a nested xen/xen/ so let's avoid that by shunning Yocto's
# extra ${PN} appended.
libexecdir = "${libdir}"

# hardcoded as Linux, as the only compatible hosts are Linux.
export XEN_OS = "Linux"

# this is used for the header (#!${bindir}/python) of the install python scripts
export PYTHONPATH="${bindir}/python"
export ac_cv_path_PYTHONPATH="${bindir}/python"

# xen and seabios require HOSTCC and HOSTCXX set to cross-compile
export HOSTCC="${BUILD_CC}"
export HOSTCXX="${BUILD_CXX}"

# make xen requires CROSS_COMPILE set by hand as it does not abide by ./configure
export CROSS_COMPILE="${TARGET_PREFIX}"

# overide LDFLAGS to allow xen to build without: "x86_64-oe-linux-ld: unrecognized option '-Wl,-O1'"
export LDFLAGS=""

# Yocto injects -mfpmath=sse for some machine types into the CFLAGS which
# conflicts with -mno-sse so instead we strip -mfpmath=sse instead of
# patching the build to be ok with this
TUNE_CCARGS := "${@oe_filter_out('-mfpmath=sse', '${TUNE_CCARGS}', d)}"

EXTRA_OECONF += " \
    --exec-prefix=/usr \
    --prefix=/usr \
    --host=${HOST_SYS} \
    --with-systemd=${systemd_unitdir}/system \
    --with-systemd-modules-load=${systemd_unitdir}/modules-load.d \
    --disable-stubdom \
    --disable-ioemu-stubdom \
    --disable-pv-grub \
    --disable-xenstore-stubdom \
    --disable-rombios \
    --disable-ocamltools \
    --with-initddir=${INIT_D_DIR} \
    --with-sysconfig-leaf-dir=default \
    --with-system-qemu=/usr/bin/qemu-system-i386 \
    --disable-qemu-traditional \
    "

EXTRA_OEMAKE += "STDVGA_ROM=${STAGING_DIR_HOST}/usr/share/firmware/vgabios-0.7a.bin"
EXTRA_OEMAKE += "CIRRUSVGA_ROM=${STAGING_DIR_HOST}/usr/share/firmware/vgabios-0.7a.cirrus.bin"
EXTRA_OEMAKE += "SEABIOS_ROM=${STAGING_DIR_HOST}/usr/share/firmware/bios.bin"
EXTRA_OEMAKE += "ETHERBOOT_ROMS=${STAGING_DIR_HOST}/usr/share/firmware/rtl8139.rom"

# prevent the Xen build scripts from fetching things during the build
# all dependencies should be reflected in the Yocto recipe
EXTRA_OEMAKE += "WGET=/bin/false"
EXTRA_OEMAKE += "GIT=/bin/false"

# check for XSM in package config to allow XSM_ENABLE to be set
python () {
    pkgconfig = d.getVar('PACKAGECONFIG', True)
    if ('xsm') in pkgconfig.split():
        d.setVar('XSM_ENABLED', '1')
    else:
        d.setVar('XSM_ENABLED', '0')
}

do_post_patch() {
    # fixup AS/CC/CCP/etc variable within StdGNU.mk
    for i in LD CC CPP CXX; do
        sed -i "s/^\($i\s\s*\).*=/\1?=/" ${S}/config/StdGNU.mk
    done
    # fixup environment passing in some makefiles
    sed -i 's#\(\w*\)=\(\$.\w*.\)#\1="\2"#' ${S}/tools/firmware/Makefile

    # libsystemd-daemon -> libsystemd for newer systemd versions
    sed -i 's#libsystemd-daemon#libsystemd#' ${S}/tools/configure
}

addtask post_patch after do_patch before do_configure

do_stubs() {
    # no stubs-32.h in our 64-bit sysroot - hack it into tools/include/gnu
    if ! test -f ${STAGING_DIR_TARGET}/usr/include/gnu/stubs-32.h ; then
        if test -f ${STAGING_DIR_TARGET}/usr/include/gnu/stubs-64.h ; then
            test -d ${S}/tools/include/gnu || mkdir ${S}/tools/include/gnu
            cat ${STAGING_DIR_TARGET}/usr/include/gnu/stubs-64.h | grep -v stub_bdflush | grep -v stub_getmsg | grep -v stub_putmsg > ${S}/tools/include/gnu/stubs-32.h
            echo \#define __stub___kernel_cosl >> ${S}/tools/include/gnu/stubs-32.h
            echo \#define __stub___kernel_sinl >> ${S}/tools/include/gnu/stubs-32.h
            echo \#define __stub___kernel_tanl >> ${S}/tools/include/gnu/stubs-32.h
        fi
    fi
}

addtask stubs after do_configure before do_compile

# Allow all hypervisor settings in a defconfig
EXTRA_OEMAKE += "XEN_CONFIG_EXPERT=y"
# Build release versions always. Technically since we track release
# tarballs this always happens but occasionally people pull in patches
# from staging that reverts this
EXTRA_OEMAKE += "debug=n"

do_configure() {

    #./configure --enable-xsmpolicy does not set XSM_ENABLE must be done manually
    if [ "${XSM_ENABLED}" = "1" ]; then
        echo "XSM_ENABLE := y" > ${S}/.config
    fi

    if [ -f "${WORKDIR}/defconfig" ]; then
        cp "${WORKDIR}/defconfig" "${B}/xen/.config" || \
        bbfatal "Unable to copy defconfig to .config"
    fi

    # do configure
    oe_runconf
}

do_compile() {
    # workaround for build bug when CFLAGS is exported
    # https://www.mail-archive.com/xen-devel@lists.xen.org/msg67822.html
    unset CFLAGS
    oe_runmake
}

do_install() {
    oe_runmake DESTDIR="${D}" install

    # remove installed volatiles
    rm -rf ${D}${localstatedir}/run \
        ${D}${localstatedir}/lock \
        ${D}${localstatedir}/log \
        ${D}${localstatedir}/volatile \
        ${D}${localstatedir}/lib/xen

    VOLATILE_DIRS=" \
        ${localstatedir}/run/xenstored \
        ${localstatedir}/run/xend \
        ${localstatedir}/run/xend/boot \
        ${localstatedir}/run/xen \
        ${localstatedir}/log/xen \
        ${localstatedir}/lock/xen \
        ${localstatedir}/lock/subsys \
        ${localstatedir}/lib/xen \
        "

    # install volatiles using populate_volatiles mechanism
    install -d ${D}${sysconfdir}/default/volatiles
    for i in $VOLATILE_DIRS; do
        echo "d root root 0755 $i none"  >> ${D}${sysconfdir}/default/volatiles/99_xen
    done

    # workaround for xendomains script which searchs sysconfig if directory exists
    install -d ${D}${sysconfdir}/sysconfig
    ln -sf ${sysconfdir}/default/xendomains ${D}${sysconfdir}/sysconfig/xendomains

    # systemd
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        # install volatiles using systemd tmpfiles.d
        install -d ${D}${sysconfdir}/tmpfiles.d
        for i in $VOLATILE_DIRS; do
            echo "d $i 0755 root root - -"  >> ${D}${sysconfdir}/tmpfiles.d/xen.conf
        done
    fi

    # fixup default path to qemu-system-i386
    sed -i 's#\(test -z "$QEMU_XEN" && QEMU_XEN=\).*$#\1"/usr/bin/qemu-system-i386"#' ${D}/etc/init.d/xencommons

    if [ -e ${D}${systemd_unitdir}/system/xen-qemu-dom0-disk-backend.service ]; then
        sed -i 's#ExecStart=.*qemu-system-i386\(.*\)$#ExecStart=/usr/bin/qemu-system-i386\1#' \
            ${D}${systemd_unitdir}/system/xen-qemu-dom0-disk-backend.service
    fi
}

pkg_postinst_${PN}-volatiles() {
    if [ -z "$D" ]; then
        if command -v systemd-tmpfiles >/dev/null; then
            systemd-tmpfiles --create ${sysconfdir}/tmpfiles.d/xen.conf
        elif [ -e ${sysconfdir}/init.d/populate-volatile.sh ]; then
            ${sysconfdir}/init.d/populate-volatile.sh update
        fi
    fi
}

do_deploy() {
    install -d ${DEPLOYDIR}

    if [ -f ${D}/boot/xen ]; then
        install -m 0644 ${D}/boot/xen ${DEPLOYDIR}/xen-${MACHINE}
    fi

    if [ -f ${D}/boot/xen.gz ]; then
        install -m 0644 ${D}/boot/xen.gz ${DEPLOYDIR}/xen-${MACHINE}.gz
    fi

    if [ -f ${D}/usr/lib64/efi/xen.efi ]; then
        install -m 0644 ${D}/usr/lib64/efi/xen.efi ${DEPLOYDIR}/xen-${MACHINE}.efi
    fi

    # Install the flask policy in the deploy directory if it exists
    if [ -f ${D}/boot/${FLASK_POLICY_FILE} ]; then
        install -m 0644 ${D}/boot/${FLASK_POLICY_FILE} ${DEPLOYDIR}
        ln -sf ${FLASK_POLICY_FILE} ${DEPLOYDIR}/xenpolicy-${MACHINE}
    fi
}

addtask deploy after do_populate_sysroot
