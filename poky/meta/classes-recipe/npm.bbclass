# Copyright (C) 2020 Savoir-Faire Linux
#
# SPDX-License-Identifier: GPL-2.0-only
#
# This bbclass builds and installs an npm package to the target. The package
# sources files should be fetched in the calling recipe by using the SRC_URI
# variable. The ${S} variable should be updated depending of your fetcher.
#
# Usage:
#  SRC_URI = "..."
#  inherit npm
#
# Optional variables:
#  NPM_ARCH:
#       Override the auto generated npm architecture.
#
#  NPM_INSTALL_DEV:
#       Set to 1 to also install devDependencies.

inherit python3native

DEPENDS:prepend = "nodejs-native nodejs-oe-cache-native "
RDEPENDS:${PN}:append:class-target = " nodejs"

EXTRA_OENPM = ""

NPM_INSTALL_DEV ?= "0"

NPM_NODEDIR ?= "${RECIPE_SYSROOT_NATIVE}${prefix_native}"

## must match mapping in nodejs.bb (openembedded-meta)
def map_nodejs_arch(a, d):
    import re

    if   re.match('i.86$', a): return 'ia32'
    elif re.match('x86_64$', a): return 'x64'
    elif re.match('aarch64$', a): return 'arm64'
    elif re.match('(powerpc64|powerpc64le|ppc64le)$', a): return 'ppc64'
    elif re.match('powerpc$', a): return 'ppc'
    return a

NPM_ARCH ?= "${@map_nodejs_arch(d.getVar("TARGET_ARCH"), d)}"

NPM_PACKAGE = "${WORKDIR}/npm-package"
NPM_CACHE = "${WORKDIR}/npm-cache"
NPM_BUILD = "${WORKDIR}/npm-build"
NPM_REGISTRY = "${WORKDIR}/npm-registry"

def npm_global_configs(d):
    """Get the npm global configuration"""
    configs = []
    # Ensure no network access is done
    configs.append(("offline", "true"))
    configs.append(("proxy", "http://invalid"))
    configs.append(("fund", False))
    configs.append(("audit", False))
    # Configure the cache directory
    configs.append(("cache", d.getVar("NPM_CACHE")))
    return configs

## 'npm pack' runs 'prepare' and 'prepack' scripts. Support for
## 'ignore-scripts' which prevents this behavior has been removed
## from nodejs 16.  Use simple 'tar' instead of.
def npm_pack(env, srcdir, workdir):
    """Emulate 'npm pack' on a specified directory"""
    import subprocess
    import os
    import json

    src = os.path.join(srcdir, 'package.json')
    with open(src) as f:
        j = json.load(f)

    # base does not really matter and is for documentation purposes
    # only.  But the 'version' part must exist because other parts of
    # the bbclass rely on it.
    base = j['name'].split('/')[-1]
    tarball = os.path.join(workdir, "%s-%s.tgz" % (base, j['version']));

    # TODO: real 'npm pack' does not include directories while 'tar'
    # does.  But this does not seem to matter...
    subprocess.run(['tar', 'czf', tarball,
                    '--exclude', './node-modules',
                    '--exclude-vcs',
                    '--transform', r's,^\./,package/,',
                    '--mtime', '1985-10-26T08:15:00.000Z',
                    '.'],
                   check = True, cwd = srcdir)

    return (tarball, j)

python npm_do_configure() {
    """
    Step one: configure the npm cache and the main npm package

    Every dependencies have been fetched and patched in the source directory.
    They have to be packed (this remove unneeded files) and added to the npm
    cache to be available for the next step.

    The main package and its associated manifest file and shrinkwrap file have
    to be configured to take into account these cached dependencies.
    """
    import base64
    import copy
    import json
    import re
    import shlex
    import stat
    import tempfile
    from bb.fetch2.npm import NpmEnvironment
    from bb.fetch2.npm import npm_unpack
    from bb.fetch2.npm import npm_package
    from bb.fetch2.npmsw import foreach_dependencies
    from bb.progress import OutOfProgressHandler
    from oe.npm_registry import NpmRegistry

    bb.utils.remove(d.getVar("NPM_CACHE"), recurse=True)
    bb.utils.remove(d.getVar("NPM_PACKAGE"), recurse=True)

    env = NpmEnvironment(d, configs=npm_global_configs(d))
    registry = NpmRegistry(d.getVar('NPM_REGISTRY'), d.getVar('NPM_CACHE'))

    def _npm_cache_add(tarball, pkg):
        """Add tarball to local registry and register it in the
           cache"""
        registry.add_pkg(tarball, pkg)

    def _npm_integrity(tarball):
        """Return the npm integrity of a specified tarball"""
        sha512 = bb.utils.sha512_file(tarball)
        return "sha512-" + base64.b64encode(bytes.fromhex(sha512)).decode()

    # Manage the manifest file and shrinkwrap files
    orig_manifest_file = d.expand("${S}/package.json")
    orig_shrinkwrap_file = d.expand("${S}/npm-shrinkwrap.json")
    cached_manifest_file = d.expand("${NPM_PACKAGE}/package.json")
    cached_shrinkwrap_file = d.expand("${NPM_PACKAGE}/npm-shrinkwrap.json")

    with open(orig_manifest_file, "r") as f:
        orig_manifest = json.load(f)

    cached_manifest = copy.deepcopy(orig_manifest)
    cached_manifest.pop("dependencies", None)
    cached_manifest.pop("devDependencies", None)

    has_shrinkwrap_file = True

    try:
        with open(orig_shrinkwrap_file, "r") as f:
            orig_shrinkwrap = json.load(f)
    except IOError:
        has_shrinkwrap_file = False

    if has_shrinkwrap_file:
       cached_shrinkwrap = copy.deepcopy(orig_shrinkwrap)
       for package in orig_shrinkwrap["packages"]:
            if package != "":
                cached_shrinkwrap["packages"].pop(package, None)
       cached_shrinkwrap["packages"][""].pop("dependencies", None)
       cached_shrinkwrap["packages"][""].pop("devDependencies", None)
       cached_shrinkwrap["packages"][""].pop("peerDependencies", None)

    # Manage the dependencies
    progress = OutOfProgressHandler(d, r"^(\d+)/(\d+)$")
    progress_total = 1 # also count the main package
    progress_done = 0

    def _count_dependency(name, params, destsuffix):
        nonlocal progress_total
        progress_total += 1

    def _cache_dependency(name, params, destsuffix):
        with tempfile.TemporaryDirectory() as tmpdir:
            # Add the dependency to the npm cache
            destdir = os.path.join(d.getVar("S"), destsuffix)
            (tarball, pkg) = npm_pack(env, destdir, tmpdir)
            _npm_cache_add(tarball, pkg)
            # Add its signature to the cached shrinkwrap
            dep = params
            dep["version"] = pkg['version']
            dep["integrity"] = _npm_integrity(tarball)
            if params.get("dev", False):
                dep["dev"] = True
                if "devDependencies" not in cached_shrinkwrap["packages"][""]:
                    cached_shrinkwrap["packages"][""]["devDependencies"] = {}
                cached_shrinkwrap["packages"][""]["devDependencies"][name] = pkg['version']

            else:
                if "dependencies" not in cached_shrinkwrap["packages"][""]:
                    cached_shrinkwrap["packages"][""]["dependencies"] = {}
                cached_shrinkwrap["packages"][""]["dependencies"][name] = pkg['version']

            cached_shrinkwrap["packages"][destsuffix] = dep
            # Display progress
            nonlocal progress_done
            progress_done += 1
            progress.write("%d/%d" % (progress_done, progress_total))

    dev = bb.utils.to_boolean(d.getVar("NPM_INSTALL_DEV"), False)

    if has_shrinkwrap_file:
        foreach_dependencies(orig_shrinkwrap, _count_dependency, dev)
        foreach_dependencies(orig_shrinkwrap, _cache_dependency, dev)
    
    # Manage Peer Dependencies
    if has_shrinkwrap_file:
        packages = orig_shrinkwrap.get("packages", {})
        peer_deps = packages.get("", {}).get("peerDependencies", {})
        package_runtime_dependencies = d.getVar("RDEPENDS:%s" % d.getVar("PN"))
        
        for peer_dep in peer_deps:
            peer_dep_yocto_name = npm_package(peer_dep)
            if peer_dep_yocto_name not in package_runtime_dependencies:
                bb.warn(peer_dep + " is a peer dependencie that is not in RDEPENDS variable. " + 
                "Please add this peer dependencie to the RDEPENDS variable as %s and generate its recipe with devtool"
                % peer_dep_yocto_name)

    # Configure the main package
    with tempfile.TemporaryDirectory() as tmpdir:
        (tarball, _) = npm_pack(env, d.getVar("S"), tmpdir)
        npm_unpack(tarball, d.getVar("NPM_PACKAGE"), d)

    # Configure the cached manifest file and cached shrinkwrap file
    def _update_manifest(depkey):
        for name in orig_manifest.get(depkey, {}):
            version = cached_shrinkwrap["packages"][""][depkey][name]
            if depkey not in cached_manifest:
                cached_manifest[depkey] = {}
            cached_manifest[depkey][name] = version

    if has_shrinkwrap_file:
        _update_manifest("dependencies")

    if dev:
        if has_shrinkwrap_file:
            _update_manifest("devDependencies")

    os.chmod(cached_manifest_file, os.stat(cached_manifest_file).st_mode | stat.S_IWUSR)
    with open(cached_manifest_file, "w") as f:
        json.dump(cached_manifest, f, indent=2)

    if has_shrinkwrap_file:
        with open(cached_shrinkwrap_file, "w") as f:
            json.dump(cached_shrinkwrap, f, indent=2)
}

python npm_do_compile() {
    """
    Step two: install the npm package

    Use the configured main package and the cached dependencies to run the
    installation process. The installation is done in a directory which is
    not the destination directory yet.

    A combination of 'npm pack' and 'npm install' is used to ensure that the
    installed files are actual copies instead of symbolic links (which is the
    default npm behavior).
    """
    import shlex
    import tempfile
    from bb.fetch2.npm import NpmEnvironment

    bb.utils.remove(d.getVar("NPM_BUILD"), recurse=True)

    with tempfile.TemporaryDirectory() as tmpdir:
        args = []
        configs = npm_global_configs(d)

        if bb.utils.to_boolean(d.getVar("NPM_INSTALL_DEV"), False):
            configs.append(("also", "development"))
        else:
            configs.append(("only", "production"))

        # Report as many logs as possible for debugging purpose
        configs.append(("loglevel", "silly"))

        # Configure the installation to be done globally in the build directory
        configs.append(("global", "true"))
        configs.append(("prefix", d.getVar("NPM_BUILD")))

        # Add node-gyp configuration
        configs.append(("arch", d.getVar("NPM_ARCH")))
        configs.append(("release", "true"))
        configs.append(("nodedir", d.getVar("NPM_NODEDIR")))
        configs.append(("python", d.getVar("PYTHON")))

        env = NpmEnvironment(d, configs)

        # Add node-pre-gyp configuration
        args.append(("target_arch", d.getVar("NPM_ARCH")))
        args.append(("build-from-source", "true"))

        # Don't install peer dependencies as they should be in RDEPENDS variable
        args.append(("legacy-peer-deps", "true"))

        # Pack and install the main package
        (tarball, _) = npm_pack(env, d.getVar("NPM_PACKAGE"), tmpdir)
        cmd = "npm install %s %s" % (shlex.quote(tarball), d.getVar("EXTRA_OENPM"))
        env.run(cmd, args=args)
}

npm_do_install() {
    # Step three: final install
    #
    # The previous installation have to be filtered to remove some extra files.

    rm -rf ${D}

    # Copy the entire lib and bin directories
    install -d ${D}/${nonarch_libdir}
    cp --no-preserve=ownership --recursive ${NPM_BUILD}/lib/. ${D}/${nonarch_libdir}

    if [ -d "${NPM_BUILD}/bin" ]
    then
        install -d ${D}/${bindir}
        cp --no-preserve=ownership --recursive ${NPM_BUILD}/bin/. ${D}/${bindir}
    fi

    # If the package (or its dependencies) uses node-gyp to build native addons,
    # object files, static libraries or other temporary files can be hidden in
    # the lib directory. To reduce the package size and to avoid QA issues
    # (staticdev with static library files) these files must be removed.
    local GYP_REGEX=".*/build/Release/[^/]*.node"

    # Remove any node-gyp directory in ${D} to remove temporary build files
    for GYP_D_FILE in $(find ${D} -regex "${GYP_REGEX}")
    do
        local GYP_D_DIR=${GYP_D_FILE%/Release/*}

        rm --recursive --force ${GYP_D_DIR}
    done

    # Copy only the node-gyp release files
    for GYP_B_FILE in $(find ${NPM_BUILD} -regex "${GYP_REGEX}")
    do
        local GYP_D_FILE=${D}/${prefix}/${GYP_B_FILE#${NPM_BUILD}}

        install -d ${GYP_D_FILE%/*}
        install -m 755 ${GYP_B_FILE} ${GYP_D_FILE}
    done

    # Remove the shrinkwrap file which does not need to be packed
    rm -f ${D}/${nonarch_libdir}/node_modules/*/npm-shrinkwrap.json
    rm -f ${D}/${nonarch_libdir}/node_modules/@*/*/npm-shrinkwrap.json
}

FILES:${PN} += " \
    ${bindir} \
    ${nonarch_libdir} \
"

EXPORT_FUNCTIONS do_configure do_compile do_install
