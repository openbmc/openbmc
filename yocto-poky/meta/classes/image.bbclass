inherit rootfs_${IMAGE_PKGTYPE}

inherit populate_sdk_ext

TOOLCHAIN_TARGET_TASK += "${PACKAGE_INSTALL}"
TOOLCHAIN_TARGET_TASK_ATTEMPTONLY += "${PACKAGE_INSTALL_ATTEMPTONLY}"
POPULATE_SDK_POST_TARGET_COMMAND += "rootfs_sysroot_relativelinks; "

inherit gzipnative

LICENSE = "MIT"
PACKAGES = ""
DEPENDS += "${MLPREFIX}qemuwrapper-cross ${MLPREFIX}depmodwrapper-cross"
RDEPENDS += "${PACKAGE_INSTALL} ${LINGUAS_INSTALL}"
RRECOMMENDS += "${PACKAGE_INSTALL_ATTEMPTONLY}"

INHIBIT_DEFAULT_DEPS = "1"

TESTIMAGECLASS = "${@base_conditional('TEST_IMAGE', '1', 'testimage-auto', '', d)}"
inherit ${TESTIMAGECLASS}

# IMAGE_FEATURES may contain any available package group
IMAGE_FEATURES ?= ""
IMAGE_FEATURES[type] = "list"
IMAGE_FEATURES[validitems] += "debug-tweaks read-only-rootfs empty-root-password allow-empty-password post-install-logging"

# Generate companion debugfs?
IMAGE_GEN_DEBUGFS ?= "0"

# rootfs bootstrap install
ROOTFS_BOOTSTRAP_INSTALL = "${@bb.utils.contains("IMAGE_FEATURES", "package-management", "", "${ROOTFS_PKGMANAGE_BOOTSTRAP}",d)}"

# packages to install from features
FEATURE_INSTALL = "${@' '.join(oe.packagegroup.required_packages(oe.data.typed_value('IMAGE_FEATURES', d), d))}"
FEATURE_INSTALL[vardepvalue] = "${FEATURE_INSTALL}"
FEATURE_INSTALL_OPTIONAL = "${@' '.join(oe.packagegroup.optional_packages(oe.data.typed_value('IMAGE_FEATURES', d), d))}"
FEATURE_INSTALL_OPTIONAL[vardepvalue] = "${FEATURE_INSTALL_OPTIONAL}"

# Define some very basic feature package groups
FEATURE_PACKAGES_package-management = "${ROOTFS_PKGMANAGE}"
SPLASH ?= "psplash"
FEATURE_PACKAGES_splash = "${SPLASH}"

IMAGE_INSTALL_COMPLEMENTARY = '${@complementary_globs("IMAGE_FEATURES", d)}'

def check_image_features(d):
    valid_features = (d.getVarFlag('IMAGE_FEATURES', 'validitems', True) or "").split()
    valid_features += d.getVarFlags('COMPLEMENTARY_GLOB').keys()
    for var in d:
       if var.startswith("PACKAGE_GROUP_"):
           bb.warn("PACKAGE_GROUP is deprecated, please use FEATURE_PACKAGES instead")
           valid_features.append(var[14:])
       elif var.startswith("FEATURE_PACKAGES_"):
           valid_features.append(var[17:])
    valid_features.sort()

    features = set(oe.data.typed_value('IMAGE_FEATURES', d))
    for feature in features:
        if feature not in valid_features:
            if bb.utils.contains('EXTRA_IMAGE_FEATURES', feature, True, False, d):
                raise bb.parse.SkipRecipe("'%s' in IMAGE_FEATURES (added via EXTRA_IMAGE_FEATURES) is not a valid image feature. Valid features: %s" % (feature, ' '.join(valid_features)))
            else:
                raise bb.parse.SkipRecipe("'%s' in IMAGE_FEATURES is not a valid image feature. Valid features: %s" % (feature, ' '.join(valid_features)))

IMAGE_INSTALL ?= ""
IMAGE_INSTALL[type] = "list"
export PACKAGE_INSTALL ?= "${IMAGE_INSTALL} ${ROOTFS_BOOTSTRAP_INSTALL} ${FEATURE_INSTALL}"
PACKAGE_INSTALL_ATTEMPTONLY ?= "${FEATURE_INSTALL_OPTIONAL}"

# Images are generally built explicitly, do not need to be part of world.
EXCLUDE_FROM_WORLD = "1"

USE_DEVFS ?= "1"
USE_DEPMOD ?= "1"

PID = "${@os.getpid()}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

LDCONFIGDEPEND ?= "ldconfig-native:do_populate_sysroot"
LDCONFIGDEPEND_libc-uclibc = ""
LDCONFIGDEPEND_libc-musl = ""

# This is needed to have depmod data in PKGDATA_DIR,
# but if you're building small initramfs image
# e.g. to include it in your kernel, you probably
# don't want this dependency, which is causing dependency loop
KERNELDEPMODDEPEND ?= "virtual/kernel:do_packagedata"

do_rootfs[depends] += " \
    makedevs-native:do_populate_sysroot virtual/fakeroot-native:do_populate_sysroot ${LDCONFIGDEPEND} \
    virtual/update-alternatives-native:do_populate_sysroot update-rc.d-native:do_populate_sysroot \
    ${KERNELDEPMODDEPEND} \
"
do_rootfs[recrdeptask] += "do_packagedata"

def command_variables(d):
    return ['ROOTFS_POSTPROCESS_COMMAND','ROOTFS_PREPROCESS_COMMAND','ROOTFS_POSTINSTALL_COMMAND','OPKG_PREPROCESS_COMMANDS','OPKG_POSTPROCESS_COMMANDS','IMAGE_POSTPROCESS_COMMAND',
            'IMAGE_PREPROCESS_COMMAND','ROOTFS_POSTPROCESS_COMMAND','POPULATE_SDK_POST_HOST_COMMAND','POPULATE_SDK_POST_TARGET_COMMAND','SDK_POSTPROCESS_COMMAND','RPM_PREPROCESS_COMMANDS',         
            'RPM_POSTPROCESS_COMMANDS']

python () {
    variables = command_variables(d)
    for var in variables:
        if d.getVar(var, False):
            d.setVarFlag(var, 'func', '1')
}

def fstype_variables(d):
    import oe.image

    image = oe.image.Image(d)
    alltypes, fstype_groups, cimages = image._get_image_types()
    fstype_vars = set()
    for fstype_group in fstype_groups:
        for fstype in fstype_group:
            fstype_vars.add('IMAGE_CMD_' + fstype)
            if fstype in cimages:
                for ctype in cimages[fstype]:
                    fstype_vars.add('COMPRESS_CMD_' + ctype)

    return sorted(fstype_vars)

def rootfs_variables(d):
    from oe.rootfs import variable_depends
    variables = ['IMAGE_DEVICE_TABLES','BUILD_IMAGES_FROM_FEEDS','IMAGE_TYPES_MASKED','IMAGE_ROOTFS_ALIGNMENT','IMAGE_OVERHEAD_FACTOR','IMAGE_ROOTFS_SIZE','IMAGE_ROOTFS_EXTRA_SPACE',
                 'IMAGE_ROOTFS_MAXSIZE','IMAGE_NAME','IMAGE_LINK_NAME','IMAGE_MANIFEST','DEPLOY_DIR_IMAGE','RM_OLD_IMAGE','IMAGE_FSTYPES','IMAGE_INSTALL_COMPLEMENTARY','IMAGE_LINGUAS','SDK_OS',
                 'SDK_OUTPUT','SDKPATHNATIVE','SDKTARGETSYSROOT','SDK_DIR','SDK_VENDOR','SDKIMAGE_INSTALL_COMPLEMENTARY','SDK_PACKAGE_ARCHS','SDK_OUTPUT','SDKTARGETSYSROOT','MULTILIBRE_ALLOW_REP',
                 'MULTILIB_TEMP_ROOTFS','MULTILIB_VARIANTS','MULTILIBS','ALL_MULTILIB_PACKAGE_ARCHS','MULTILIB_GLOBAL_VARIANTS','BAD_RECOMMENDATIONS','NO_RECOMMENDATIONS','PACKAGE_ARCHS',
                 'PACKAGE_CLASSES','TARGET_VENDOR','TARGET_VENDOR','TARGET_ARCH','TARGET_OS','OVERRIDES','BBEXTENDVARIANT','FEED_DEPLOYDIR_BASE_URI','INTERCEPT_DIR','USE_DEVFS',
                 'COMPRESSIONTYPES', 'IMAGE_GEN_DEBUGFS']
    variables.extend(fstype_variables(d))
    variables.extend(command_variables(d))
    variables.extend(variable_depends(d))
    return " ".join(variables)

do_rootfs[vardeps] += "${@rootfs_variables(d)}"

do_build[depends] += "virtual/kernel:do_deploy"

def build_live(d):
    if bb.utils.contains("IMAGE_FSTYPES", "live", "live", "0", d) == "0": # live is not set but hob might set iso or hddimg
        d.setVar('NOISO', bb.utils.contains('IMAGE_FSTYPES', "iso", "0", "1", d))
        d.setVar('NOHDD', bb.utils.contains('IMAGE_FSTYPES', "hddimg", "0", "1", d))
        if d.getVar('NOISO', True) == "0" or d.getVar('NOHDD', True) == "0":
            return "image-live"
        return ""
    return "image-live"

IMAGE_TYPE_live = "${@build_live(d)}"
inherit ${IMAGE_TYPE_live}

IMAGE_TYPE_vm = '${@bb.utils.contains_any("IMAGE_FSTYPES", ["vmdk", "vdi", "qcow2", "hdddirect"], "image-vm", "", d)}'
inherit ${IMAGE_TYPE_vm}

python () {
    deps = " " + imagetypes_getdepends(d)
    d.appendVarFlag('do_rootfs', 'depends', deps)

    deps = ""
    for dep in (d.getVar('EXTRA_IMAGEDEPENDS', True) or "").split():
        deps += " %s:do_populate_sysroot" % dep
    d.appendVarFlag('do_build', 'depends', deps)

    #process IMAGE_FEATURES, we must do this before runtime_mapping_rename
    #Check for replaces image features
    features = set(oe.data.typed_value('IMAGE_FEATURES', d))
    remain_features = features.copy()
    for feature in features:
        replaces = set((d.getVar("IMAGE_FEATURES_REPLACES_%s" % feature, True) or "").split())
        remain_features -= replaces

    #Check for conflict image features
    for feature in remain_features:
        conflicts = set((d.getVar("IMAGE_FEATURES_CONFLICTS_%s" % feature, True) or "").split())
        temp = conflicts & remain_features
        if temp:
            bb.fatal("%s contains conflicting IMAGE_FEATURES %s %s" % (d.getVar('PN', True), feature, ' '.join(list(temp))))

    d.setVar('IMAGE_FEATURES', ' '.join(list(remain_features)))

    check_image_features(d)
    initramfs_image = d.getVar('INITRAMFS_IMAGE', True) or ""
    if initramfs_image != "":
        d.appendVarFlag('do_build', 'depends', " %s:do_bundle_initramfs" %  d.getVar('PN', True))
        d.appendVarFlag('do_bundle_initramfs', 'depends', " %s:do_rootfs" % initramfs_image)
}

IMAGE_CLASSES += "image_types"
inherit ${IMAGE_CLASSES}

IMAGE_POSTPROCESS_COMMAND ?= ""

# Zap the root password if debug-tweaks feature is not enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'empty-root-password' ], "", "zap_empty_root_password ; ",d)}'

# Allow dropbear/openssh to accept logins from accounts with an empty password string if debug-tweaks is enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'allow-empty-password' ], "ssh_allow_empty_password; ", "",d)}'

# Enable postinst logging if debug-tweaks is enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains_any("IMAGE_FEATURES", [ 'debug-tweaks', 'post-install-logging' ], "postinst_enable_logging; ", "",d)}'

# Create /etc/timestamp during image construction to give a reasonably sane default time setting
ROOTFS_POSTPROCESS_COMMAND += "rootfs_update_timestamp ; "

# Tweak the mount options for rootfs in /etc/fstab if read-only-rootfs is enabled
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "read-only-rootfs", "read_only_rootfs_hook; ", "",d)}'

# Write manifest
IMAGE_MANIFEST = "${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.manifest"
ROOTFS_POSTUNINSTALL_COMMAND =+ "write_image_manifest ; "
# Set default postinst log file
POSTINST_LOGFILE ?= "${localstatedir}/log/postinstall.log"
# Set default target for systemd images
SYSTEMD_DEFAULT_TARGET ?= '${@bb.utils.contains("IMAGE_FEATURES", "x11-base", "graphical.target", "multi-user.target", d)}'
ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("DISTRO_FEATURES", "systemd", "set_systemd_default_target; ", "", d)}'

ROOTFS_POSTPROCESS_COMMAND += 'empty_var_volatile;'

# some default locales
IMAGE_LINGUAS ?= "de-de fr-fr en-gb"

LINGUAS_INSTALL ?= "${@" ".join(map(lambda s: "locale-base-%s" % s, d.getVar('IMAGE_LINGUAS', True).split()))}"

# Prefer image, but use the fallback files for lookups if the image ones
# aren't yet available.
PSEUDO_PASSWD = "${IMAGE_ROOTFS}:${STAGING_DIR_NATIVE}"

do_rootfs[dirs] = "${TOPDIR}"
do_rootfs[lockfiles] += "${IMAGE_ROOTFS}.lock"
do_rootfs[cleandirs] += "${S}"

# Must call real_do_rootfs() from inside here, rather than as a separate
# task, so that we have a single fakeroot context for the whole process.
do_rootfs[umask] = "022"

# A hook function to support read-only-rootfs IMAGE_FEATURES
read_only_rootfs_hook () {
	# Tweak the mount option and fs_passno for rootfs in fstab
	sed -i -e '/^[#[:space:]]*\/dev\/root/{s/defaults/ro/;s/\([[:space:]]*[[:digit:]]\)\([[:space:]]*\)[[:digit:]]$/\1\20/}' ${IMAGE_ROOTFS}/etc/fstab

	# If we're using openssh and the /etc/ssh directory has no pre-generated keys,
	# we should configure openssh to use the configuration file /etc/ssh/sshd_config_readonly
	# and the keys under /var/run/ssh.
	if [ -d ${IMAGE_ROOTFS}/etc/ssh ]; then
		if [ -e ${IMAGE_ROOTFS}/etc/ssh/ssh_host_rsa_key ]; then
			echo "SYSCONFDIR=/etc/ssh" >> ${IMAGE_ROOTFS}/etc/default/ssh
			echo "SSHD_OPTS=" >> ${IMAGE_ROOTFS}/etc/default/ssh
		else
			echo "SYSCONFDIR=/var/run/ssh" >> ${IMAGE_ROOTFS}/etc/default/ssh
			echo "SSHD_OPTS='-f /etc/ssh/sshd_config_readonly'" >> ${IMAGE_ROOTFS}/etc/default/ssh
		fi
	fi

	# Also tweak the key location for dropbear in the same way.
	if [ -d ${IMAGE_ROOTFS}/etc/dropbear ]; then
		if [ -e ${IMAGE_ROOTFS}/etc/dropbear/dropbear_rsa_host_key ]; then
			echo "DROPBEAR_RSAKEY_DIR=/etc/dropbear" >> ${IMAGE_ROOTFS}/etc/default/dropbear
		else
			echo "DROPBEAR_RSAKEY_DIR=/var/lib/dropbear" >> ${IMAGE_ROOTFS}/etc/default/dropbear
		fi
	fi


	if ${@bb.utils.contains("DISTRO_FEATURES", "sysvinit", "true", "false", d)}; then
		# Change the value of ROOTFS_READ_ONLY in /etc/default/rcS to yes
		if [ -e ${IMAGE_ROOTFS}/etc/default/rcS ]; then
			sed -i 's/ROOTFS_READ_ONLY=no/ROOTFS_READ_ONLY=yes/' ${IMAGE_ROOTFS}/etc/default/rcS
		fi
		# Run populate-volatile.sh at rootfs time to set up basic files
		# and directories to support read-only rootfs.
		if [ -x ${IMAGE_ROOTFS}/etc/init.d/populate-volatile.sh ]; then
			${IMAGE_ROOTFS}/etc/init.d/populate-volatile.sh
		fi
	fi

	if ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "true", "false", d)}; then
	    # Update user database files so that services don't fail for a read-only systemd system
	    for conffile in ${IMAGE_ROOTFS}/usr/lib/sysusers.d/systemd.conf ${IMAGE_ROOTFS}/usr/lib/sysusers.d/systemd-remote.conf; do
		[ -e $conffile ] || continue
		grep -v "^#" $conffile | sed -e '/^$/d' | while read type name id comment; do
		    if [ "$type" = "u" ]; then
			useradd_params=""
			[ "$id" != "-" ] && useradd_params="$useradd_params --uid $id"
			[ "$comment" != "-" ] && useradd_params="$useradd_params --comment $comment"
			useradd_params="$useradd_params --system $name"
			eval useradd --root ${IMAGE_ROOTFS} $useradd_params || true
		    elif [ "$type" = "g" ]; then
			groupadd_params=""
			[ "$id" != "-" ] && groupadd_params="$groupadd_params --gid $id"
			groupadd_params="$groupadd_params --system $name"
			eval groupadd --root ${IMAGE_ROOTFS} $groupadd_params || true
		    fi
		done
	    done
	fi
}

PACKAGE_EXCLUDE ??= ""
PACKAGE_EXCLUDE[type] = "list"

python rootfs_process_ignore() {
    excl_pkgs = d.getVar("PACKAGE_EXCLUDE", True).split()
    inst_pkgs = d.getVar("PACKAGE_INSTALL", True).split()
    inst_attempt_pkgs = d.getVar("PACKAGE_INSTALL_ATTEMPTONLY", True).split()

    d.setVar('PACKAGE_INSTALL_ORIG', ' '.join(inst_pkgs))
    d.setVar('PACKAGE_INSTALL_ATTEMPTONLY', ' '.join(inst_attempt_pkgs))

    for pkg in excl_pkgs:
        if pkg in inst_pkgs:
            bb.warn("Package %s, set to be excluded, is in %s PACKAGE_INSTALL (%s).  It will be removed from the list." % (pkg, d.getVar('PN', True), inst_pkgs))
            inst_pkgs.remove(pkg)

        if pkg in inst_attempt_pkgs:
            bb.warn("Package %s, set to be excluded, is in %s PACKAGE_INSTALL_ATTEMPTONLY (%s).  It will be removed from the list." % (pkg, d.getVar('PN', True), inst_pkgs))
            inst_attempt_pkgs.remove(pkg)

    d.setVar("PACKAGE_INSTALL", ' '.join(inst_pkgs))
    d.setVar("PACKAGE_INSTALL_ATTEMPTONLY", ' '.join(inst_attempt_pkgs))
}
do_rootfs[prefuncs] += "rootfs_process_ignore"

# We have to delay the runtime_mapping_rename until just before rootfs runs
# otherwise, the multilib renaming could step in and squash any fixups that
# may have occurred.
python rootfs_runtime_mapping() {
    pn = d.getVar('PN', True)
    runtime_mapping_rename("PACKAGE_INSTALL", pn, d)
    runtime_mapping_rename("PACKAGE_INSTALL_ATTEMPTONLY", pn, d)
    runtime_mapping_rename("BAD_RECOMMENDATIONS", pn, d)
}
do_rootfs[prefuncs] += "rootfs_runtime_mapping"

fakeroot python do_rootfs () {
    from oe.rootfs import create_rootfs
    from oe.image import create_image
    from oe.manifest import create_manifest

    # generate the initial manifest
    create_manifest(d)

    # generate rootfs
    create_rootfs(d)

    # generate final images
    create_image(d)
}

insert_feed_uris () {
	
	echo "Building feeds for [${DISTRO}].."

	for line in ${FEED_URIS}
	do
		# strip leading and trailing spaces/tabs, then split into name and uri
		line_clean="`echo "$line"|sed 's/^[ \t]*//;s/[ \t]*$//'`"
		feed_name="`echo "$line_clean" | sed -n 's/\(.*\)##\(.*\)/\1/p'`"
		feed_uri="`echo "$line_clean" | sed -n 's/\(.*\)##\(.*\)/\2/p'`"
		
		echo "Added $feed_name feed with URL $feed_uri"
		
		# insert new feed-sources
		echo "src/gz $feed_name $feed_uri" >> ${IMAGE_ROOTFS}/etc/opkg/${feed_name}-feed.conf
	done
}

MULTILIBRE_ALLOW_REP =. "${base_bindir}|${base_sbindir}|${bindir}|${sbindir}|${libexecdir}|${sysconfdir}|${nonarch_base_libdir}/udev|/lib/modules/[^/]*/modules.*|"
MULTILIB_CHECK_FILE = "${WORKDIR}/multilib_check.py"
MULTILIB_TEMP_ROOTFS = "${WORKDIR}/multilib"

# This function is intended to disallow empty root password if 'debug-tweaks' is not in IMAGE_FEATURES.
zap_empty_root_password () {
	if [ -e ${IMAGE_ROOTFS}/etc/shadow ]; then
		sed -i 's%^root::%root:*:%' ${IMAGE_ROOTFS}/etc/shadow
        fi
	if [ -e ${IMAGE_ROOTFS}/etc/passwd ]; then
		sed -i 's%^root::%root:*:%' ${IMAGE_ROOTFS}/etc/passwd
	fi
} 

# allow dropbear/openssh to accept root logins and logins from accounts with an empty password string
ssh_allow_empty_password () {
	if [ -e ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config ]; then
		sed -i 's/^[#[:space:]]*PermitRootLogin.*/PermitRootLogin yes/' ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config
		sed -i 's/^[#[:space:]]*PermitEmptyPasswords.*/PermitEmptyPasswords yes/' ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config
	fi

	if [ -e ${IMAGE_ROOTFS}${sbindir}/dropbear ] ; then
		if grep -q DROPBEAR_EXTRA_ARGS ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear 2>/dev/null ; then
			if ! grep -q "DROPBEAR_EXTRA_ARGS=.*-B" ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear ; then
				sed -i 's/^DROPBEAR_EXTRA_ARGS="*\([^"]*\)"*/DROPBEAR_EXTRA_ARGS="\1 -B"/' ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear
			fi
		else
			printf '\nDROPBEAR_EXTRA_ARGS="-B"\n' >> ${IMAGE_ROOTFS}${sysconfdir}/default/dropbear
		fi
	fi

	if [ -d ${IMAGE_ROOTFS}${sysconfdir}/pam.d ] ; then
		sed -i 's/nullok_secure/nullok/' ${IMAGE_ROOTFS}${sysconfdir}/pam.d/*
	fi
}

# Disable DNS lookups, the SSH_DISABLE_DNS_LOOKUP can be overridden to allow
# distros to choose not to take this change
SSH_DISABLE_DNS_LOOKUP ?= " ssh_disable_dns_lookup ; "
ROOTFS_POSTPROCESS_COMMAND_append_qemuall = "${SSH_DISABLE_DNS_LOOKUP}"
ssh_disable_dns_lookup () {
	if [ -e ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config ]; then
		sed -i -e 's:#UseDNS yes:UseDNS no:' ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config
	fi
}

# Enable postinst logging if debug-tweaks is enabled
postinst_enable_logging () {
	mkdir -p ${IMAGE_ROOTFS}${sysconfdir}/default
	echo "POSTINST_LOGGING=1" >> ${IMAGE_ROOTFS}${sysconfdir}/default/postinst
	echo "LOGFILE=${POSTINST_LOGFILE}" >> ${IMAGE_ROOTFS}${sysconfdir}/default/postinst
}

# Modify systemd default target
set_systemd_default_target () {
	if [ -d ${IMAGE_ROOTFS}${sysconfdir}/systemd/system -a -e ${IMAGE_ROOTFS}${systemd_unitdir}/system/${SYSTEMD_DEFAULT_TARGET} ]; then
		ln -sf ${systemd_unitdir}/system/${SYSTEMD_DEFAULT_TARGET} ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/default.target
	fi
}

# If /var/volatile is not empty, we have seen problems where programs such as the
# journal make assumptions based on the contents of /var/volatile. The journal
# would then write to /var/volatile before it was mounted, thus hiding the
# items previously written.
#
# This change is to attempt to fix those types of issues in a way that doesn't
# affect users that may not be using /var/volatile.
empty_var_volatile () {
	if [ -e ${IMAGE_ROOTFS}/etc/fstab ]; then
		match=`awk '$1 !~ "#" && $2 ~ /\/var\/volatile/{print $2}' ${IMAGE_ROOTFS}/etc/fstab 2> /dev/null`
		if [ -n "$match" ]; then
			find ${IMAGE_ROOTFS}/var/volatile -mindepth 1 -delete
		fi
	fi
}

# Turn any symbolic /sbin/init link into a file
remove_init_link () {
	if [ -h ${IMAGE_ROOTFS}/sbin/init ]; then
		LINKFILE=${IMAGE_ROOTFS}`readlink ${IMAGE_ROOTFS}/sbin/init`
		rm ${IMAGE_ROOTFS}/sbin/init
		cp $LINKFILE ${IMAGE_ROOTFS}/sbin/init
	fi
}

make_zimage_symlink_relative () {
	if [ -L ${IMAGE_ROOTFS}/boot/zImage ]; then
		(cd ${IMAGE_ROOTFS}/boot/ && for i in `ls zImage-* | sort`; do ln -sf $i zImage; done)
	fi
}

python write_image_manifest () {
    from oe.rootfs import image_list_installed_packages
    with open(d.getVar('IMAGE_MANIFEST', True), 'w+') as image_manifest:
        image_manifest.write(image_list_installed_packages(d, 'ver'))
        image_manifest.write("\n")
}

# Can be use to create /etc/timestamp during image construction to give a reasonably 
# sane default time setting
rootfs_update_timestamp () {
	date -u +%4Y%2m%2d%2H%2M%2S >${IMAGE_ROOTFS}/etc/timestamp
}

# Prevent X from being started
rootfs_no_x_startup () {
	if [ -f ${IMAGE_ROOTFS}/etc/init.d/xserver-nodm ]; then
		chmod a-x ${IMAGE_ROOTFS}/etc/init.d/xserver-nodm
	fi
}

rootfs_trim_schemas () {
	for schema in ${IMAGE_ROOTFS}/etc/gconf/schemas/*.schemas
	do
		# Need this in case no files exist
		if [ -e $schema ]; then
			oe-trim-schemas $schema > $schema.new
			mv $schema.new $schema
		fi
	done
}

rootfs_check_host_user_contaminated () {
	contaminated="${WORKDIR}/host-user-contaminated.txt"
	HOST_USER_UID="$(PSEUDO_UNLOAD=1 id -u)"
	HOST_USER_GID="$(PSEUDO_UNLOAD=1 id -g)"

	find "${IMAGE_ROOTFS}" -wholename "${IMAGE_ROOTFS}/home" -prune \
	    -user "$HOST_USER_UID" -o -group "$HOST_USER_GID" >"$contaminated"

	if [ -s "$contaminated" ]; then
		echo "WARNING: Paths in the rootfs are owned by the same user or group as the user running bitbake. See the logfile for the specific paths."
		cat "$contaminated" | sed "s,^,  ,"
	fi
}

# Make any absolute links in a sysroot relative
rootfs_sysroot_relativelinks () {
	sysroot-relativelinks.py ${SDK_OUTPUT}/${SDKTARGETSYSROOT}
}

do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_package_qa[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

addtask rootfs before do_build
# Allow the kernel to be repacked with the initramfs and boot image file as a single file
do_bundle_initramfs[depends] += "virtual/kernel:do_bundle_initramfs"
do_bundle_initramfs[nostamp] = "1"
do_bundle_initramfs[noexec] = "1"
do_bundle_initramfs () {
	:
}
addtask bundle_initramfs after do_rootfs
