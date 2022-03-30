python do_rust_gen_targets () {
    wd = d.getVar('WORKDIR') + '/targets/'
    # It is important 'TARGET' is last here so that it overrides our less
    # informed choices for BUILD & HOST if TARGET happens to be the same as
    # either of them.
    for thing in ['BUILD', 'HOST', 'TARGET']:
        bb.debug(1, "rust_gen_target for " + thing)
        features = ""
        cpu = "generic"
        arch = d.getVar('{}_ARCH'.format(thing))
        abi = ""
        if thing is "TARGET":
            abi = d.getVar('ABIEXTENSION')
            # arm and armv7 have different targets in llvm
            if arch == "arm" and target_is_armv7(d):
                arch = 'armv7'
            features = d.getVar('TARGET_LLVM_FEATURES') or ""
            cpu = d.getVar('TARGET_LLVM_CPU')
        rust_gen_target(d, thing, wd, features, cpu, arch, abi)
}

# Otherwise we'll depend on what we provide
INHIBIT_DEFAULT_RUST_DEPS = "1"

# Unlike native (which nicely maps it's DEPENDS) cross wipes them out completely.
# Generally, we (and cross in general) need the same things that native needs,
# so it might make sense to take it's mapping. For now, though, we just mention
# the bits we need explicitly.
DEPENDS += "rust-llvm-native"
DEPENDS += "rust-native"

# In the cross compilation case, rustc doesn't seem to get the rpath quite
# right. It manages to include '../../lib/${TARGET_PREFIX}', but doesn't
# include the '../../lib' (ie: relative path from cross_bindir to normal
# libdir. As a result, we end up not being able to properly reference files in normal ${libdir}.
# Most of the time this happens to work fine as the systems libraries are
# subsituted, but sometimes a host system will lack a library, or the right
# version of a library (libtinfo was how I noticed this).
#
# FIXME: this should really be fixed in rust itself.
# FIXME: using hard-coded relative paths is wrong, we should ask bitbake for
#        the relative path between 2 of it's vars.
HOST_POST_LINK_ARGS:append = " -Wl,-rpath=../../lib"
BUILD_POST_LINK_ARGS:append = " -Wl,-rpath=../../lib"

# We need the same thing for the calls to the compiler when building the runtime crap
TARGET_CC_ARCH:append = " --sysroot=${STAGING_DIR_TARGET}"

do_rust_setup_snapshot () {
}

do_configure () {
}

do_compile () {
}

do_install () {
	mkdir -p ${D}${prefix}/${base_libdir_native}/rustlib
	cp ${WORKDIR}/targets/${TARGET_SYS}.json ${D}${prefix}/${base_libdir_native}/rustlib
}

rust_cross_sysroot_preprocess() {
    sysroot_stage_dir ${D}${prefix}/${base_libdir_native}/rustlib ${SYSROOT_DESTDIR}${prefix}/${base_libdir_native}/rustlib
}
SYSROOT_PREPROCESS_FUNCS += "rust_cross_sysroot_preprocess"
