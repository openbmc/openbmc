#
# Toaster helper class
#
# Copyright (C) 2013 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)
#
# This bbclass is designed to extract data used by OE-Core during the build process,
# for recording in the Toaster system.
# The data access is synchronous, preserving the build data integrity across
# different builds.
#
# The data is transferred through the event system, using the MetadataEvent objects.
#
# The model is to enable the datadump functions as postfuncs, and have the dump
# executed after the real taskfunc has been executed. This prevents task signature changing
# is toaster is enabled or not. Build performance is not affected if Toaster is not enabled.
#
# To enable, use INHERIT in local.conf:
#
#       INHERIT += "toaster"
#
#
#
#

# Find and dump layer info when we got the layers parsed



python toaster_layerinfo_dumpdata() {
    import subprocess

    def _get_git_branch(layer_path):
        branch = subprocess.Popen("git symbolic-ref HEAD 2>/dev/null ", cwd=layer_path, shell=True, stdout=subprocess.PIPE).communicate()[0]
        branch = branch.decode('utf-8')
        branch = branch.replace('refs/heads/', '').rstrip()
        return branch

    def _get_git_revision(layer_path):
        revision = subprocess.Popen("git rev-parse HEAD 2>/dev/null ", cwd=layer_path, shell=True, stdout=subprocess.PIPE).communicate()[0].rstrip()
        return revision

    def _get_url_map_name(layer_name):
        """ Some layers have a different name on openembedded.org site,
            this method returns the correct name to use in the URL
        """

        url_name = layer_name
        url_mapping = {'meta': 'openembedded-core'}

        for key in url_mapping.keys():
            if key == layer_name:
                url_name = url_mapping[key]

        return url_name

    def _get_layer_version_information(layer_path):

        layer_version_info = {}
        layer_version_info['branch'] = _get_git_branch(layer_path)
        layer_version_info['commit'] = _get_git_revision(layer_path)
        layer_version_info['priority'] = 0

        return layer_version_info


    def _get_layer_dict(layer_path):

        layer_info = {}
        layer_name = layer_path.split('/')[-1]
        layer_url = 'http://layers.openembedded.org/layerindex/layer/{layer}/'
        layer_url_name = _get_url_map_name(layer_name)

        layer_info['name'] = layer_url_name
        layer_info['local_path'] = layer_path
        layer_info['layer_index_url'] = layer_url.format(layer=layer_url_name)
        layer_info['version'] = _get_layer_version_information(layer_path)

        return layer_info


    bblayers = e.data.getVar("BBLAYERS")

    llayerinfo = {}

    for layer in { l for l in bblayers.strip().split(" ") if len(l) }:
        llayerinfo[layer] = _get_layer_dict(layer)


    bb.event.fire(bb.event.MetadataEvent("LayerInfo", llayerinfo), e.data)
}

# Dump package file info data

def _toaster_load_pkgdatafile(dirpath, filepath):
    import json
    import re
    pkgdata = {}
    with open(os.path.join(dirpath, filepath), "r") as fin:
        for line in fin:
            try:
                kn, kv = line.strip().split(": ", 1)
                m = re.match(r"^PKG_([^A-Z:]*)", kn)
                if m:
                    pkgdata['OPKGN'] = m.group(1)
                kn = "_".join([x for x in kn.split("_") if x.isupper()])
                pkgdata[kn] = kv.strip()
                if kn == 'FILES_INFO':
                    pkgdata[kn] = json.loads(kv)

            except ValueError:
                pass    # ignore lines without valid key: value pairs
    return pkgdata

python toaster_package_dumpdata() {
    """
    Dumps the data about the packages created by a recipe
    """

    # No need to try and dumpdata if the recipe isn't generating packages
    if not d.getVar('PACKAGES'):
        return

    pkgdatadir = d.getVar('PKGDESTWORK')
    lpkgdata = {}
    datadir = os.path.join(pkgdatadir, 'runtime')

    # scan and send data for each generated package
    for datafile in os.listdir(datadir):
        if not datafile.endswith('.packaged'):
            lpkgdata = _toaster_load_pkgdatafile(datadir, datafile)
            # Fire an event containing the pkg data
            bb.event.fire(bb.event.MetadataEvent("SinglePackageInfo", lpkgdata), d)
}

# 2. Dump output image files information

python toaster_artifact_dumpdata() {
    """
    Dump data about SDK variables
    """

    event_data = {
      "TOOLCHAIN_OUTPUTNAME": d.getVar("TOOLCHAIN_OUTPUTNAME")
    }

    bb.event.fire(bb.event.MetadataEvent("SDKArtifactInfo", event_data), d)
}

# collect list of buildstats files based on fired events; when the build completes, collect all stats and fire an event with collected data

python toaster_collect_task_stats() {
    import bb.build
    import bb.event
    import bb.data
    import bb.utils
    import os

    if not e.data.getVar('BUILDSTATS_BASE'):
        return  # if we don't have buildstats, we cannot collect stats

    toaster_statlist_file = os.path.join(e.data.getVar('BUILDSTATS_BASE'), "toasterstatlist")

    def stat_to_float(value):
        return float(value.strip('% \n\r'))

    def _append_read_list(v):
        lock = bb.utils.lockfile(e.data.expand("${TOPDIR}/toaster.lock"), False, True)

        with open(toaster_statlist_file, "a") as fout:
            taskdir = e.data.expand("${BUILDSTATS_BASE}/${BUILDNAME}/${PF}")
            fout.write("%s::%s::%s::%s\n" % (e.taskfile, e.taskname, os.path.join(taskdir, e.task), e.data.expand("${PN}")))

        bb.utils.unlockfile(lock)

    def _read_stats(filename):
        # seconds
        cpu_time_user = 0
        cpu_time_system = 0

        # bytes
        disk_io_read = 0
        disk_io_write = 0

        started = 0
        ended = 0

        taskname = ''

        statinfo = {}

        with open(filename, 'r') as task_bs:
            for line in task_bs.readlines():
                k,v = line.strip().split(": ", 1)
                statinfo[k] = v

        if "Started" in statinfo:
            started = stat_to_float(statinfo["Started"])

        if "Ended" in statinfo:
            ended = stat_to_float(statinfo["Ended"])

        if "Child rusage ru_utime" in statinfo:
            cpu_time_user = cpu_time_user + stat_to_float(statinfo["Child rusage ru_utime"])

        if "Child rusage ru_stime" in statinfo:
            cpu_time_system = cpu_time_system + stat_to_float(statinfo["Child rusage ru_stime"])

        if "IO write_bytes" in statinfo:
            write_bytes = int(statinfo["IO write_bytes"].strip('% \n\r'))
            disk_io_write = disk_io_write + write_bytes

        if "IO read_bytes" in statinfo:
            read_bytes = int(statinfo["IO read_bytes"].strip('% \n\r'))
            disk_io_read = disk_io_read + read_bytes

        return {
            'stat_file': filename,
            'cpu_time_user': cpu_time_user,
            'cpu_time_system': cpu_time_system,
            'disk_io_read': disk_io_read,
            'disk_io_write': disk_io_write,
            'started': started,
            'ended': ended
        }

    if isinstance(e, (bb.build.TaskSucceeded, bb.build.TaskFailed)):
        _append_read_list(e)
        pass

    if isinstance(e, bb.event.BuildCompleted) and os.path.exists(toaster_statlist_file):
        events = []
        with open(toaster_statlist_file, "r") as fin:
            for line in fin:
                (taskfile, taskname, filename, recipename) = line.strip().split("::")
                stats = _read_stats(filename)
                events.append((taskfile, taskname, stats, recipename))
        bb.event.fire(bb.event.MetadataEvent("BuildStatsList", events), e.data)
        os.unlink(toaster_statlist_file)
}

# dump relevant build history data as an event when the build is completed

python toaster_buildhistory_dump() {
    import re
    BUILDHISTORY_DIR = e.data.expand("${TOPDIR}/buildhistory")
    BUILDHISTORY_DIR_IMAGE_BASE = e.data.expand("%s/images/${MACHINE_ARCH}/${TCLIBC}/"% BUILDHISTORY_DIR)
    pkgdata_dir = e.data.getVar("PKGDATA_DIR")


    # scan the build targets for this build
    images = {}
    allpkgs = {}
    files = {}
    for target in e._pkgs:
        target = target.split(':')[0] # strip ':<task>' suffix from the target
        installed_img_path = e.data.expand(os.path.join(BUILDHISTORY_DIR_IMAGE_BASE, target))
        if os.path.exists(installed_img_path):
            images[target] = {}
            files[target] = {}
            files[target]['dirs'] = []
            files[target]['syms'] = []
            files[target]['files'] = []
            with open("%s/installed-package-sizes.txt" % installed_img_path, "r") as fin:
                for line in fin:
                    line = line.rstrip(";")
                    psize, punit, pname = line.split()
                    # this size is "installed-size" as it measures how much space it takes on disk
                    images[target][pname.strip()] = {'size':int(psize)*1024, 'depends' : []}

            with open("%s/depends.dot" % installed_img_path, "r") as fin:
                p = re.compile(r'\s*"(?P<name>[^"]+)"\s*->\s*"(?P<dep>[^"]+)"(?P<rec>.*?\[style=dotted\])?')
                for line in fin:
                    m = p.match(line)
                    if not m:
                        continue
                    pname = m.group('name')
                    dependsname = m.group('dep')
                    deptype = 'recommends' if m.group('rec') else 'depends'

                    # If RPM is used for packaging, then there may be
                    # dependencies such as "/bin/sh", which will confuse
                    # _toaster_load_pkgdatafile() later on. While at it, ignore
                    # any dependencies that contain parentheses, e.g.,
                    # "libc.so.6(GLIBC_2.7)".
                    if dependsname.startswith('/') or '(' in dependsname:
                        continue

                    if not pname in images[target]:
                        images[target][pname] = {'size': 0, 'depends' : []}
                    if not dependsname in images[target]:
                        images[target][dependsname] = {'size': 0, 'depends' : []}
                    images[target][pname]['depends'].append((dependsname, deptype))

            # files-in-image.txt is only generated if an image file is created,
            # so the file entries ('syms', 'dirs', 'files') for a target will be
            # empty for rootfs builds and other "image" tasks which don't
            # produce image files
            # (e.g. "bitbake core-image-minimal -c populate_sdk")
            files_in_image_path = "%s/files-in-image.txt" % installed_img_path
            if os.path.exists(files_in_image_path):
                with open(files_in_image_path, "r") as fin:
                    for line in fin:
                        lc = [ x for x in line.strip().split(" ") if len(x) > 0 ]
                        if lc[0].startswith("l"):
                            files[target]['syms'].append(lc)
                        elif lc[0].startswith("d"):
                            files[target]['dirs'].append(lc)
                        else:
                            files[target]['files'].append(lc)

            for pname in images[target]:
                if not pname in allpkgs:
                    try:
                        pkgdata = _toaster_load_pkgdatafile("%s/runtime-reverse/" % pkgdata_dir, pname)
                    except IOError as err:
                        if err.errno == 2:
                            # We expect this e.g. for RRECOMMENDS that are unsatisfied at runtime
                            continue
                        else:
                            raise
                    allpkgs[pname] = pkgdata


    data = { 'pkgdata' : allpkgs, 'imgdata' : images, 'filedata' : files }

    bb.event.fire(bb.event.MetadataEvent("ImagePkgList", data), e.data)

}

# get list of artifacts from sstate manifest
python toaster_artifacts() {
    if e.taskname in ["do_deploy", "do_image_complete", "do_populate_sdk", "do_populate_sdk_ext"]:
        d2 = d.createCopy()
        d2.setVar('FILE', e.taskfile)
        # Use 'stamp-extra-info' if present, else use workaround
        # to determine 'SSTATE_MANMACH'
        extrainf = d2.getVarFlag(e.taskname, 'stamp-extra-info')
        if extrainf:
            d2.setVar('SSTATE_MANMACH', extrainf)
        else:
            if "do_populate_sdk" == e.taskname:
                d2.setVar('SSTATE_MANMACH', d2.expand("${MACHINE}${SDKMACHINE}"))
            else:
                d2.setVar('SSTATE_MANMACH', d2.expand("${MACHINE}"))
        manifest = oe.sstatesig.sstate_get_manifest_filename(e.taskname[3:], d2)[0]

        if os.access(manifest, os.R_OK):
            with open(manifest) as fmanifest:
                artifacts = [fname.strip() for fname in fmanifest]
                data = {"task": e.taskid, "artifacts": artifacts}
                bb.event.fire(bb.event.MetadataEvent("TaskArtifacts", data), d2)
}

# set event handlers
addhandler toaster_layerinfo_dumpdata
toaster_layerinfo_dumpdata[eventmask] = "bb.event.TreeDataPreparationCompleted"

addhandler toaster_collect_task_stats
toaster_collect_task_stats[eventmask] = "bb.event.BuildCompleted bb.build.TaskSucceeded bb.build.TaskFailed"

addhandler toaster_buildhistory_dump
toaster_buildhistory_dump[eventmask] = "bb.event.BuildCompleted"

addhandler toaster_artifacts
toaster_artifacts[eventmask] = "bb.runqueue.runQueueTaskSkipped bb.runqueue.runQueueTaskCompleted"

do_packagedata_setscene[postfuncs] += "toaster_package_dumpdata "
do_packagedata_setscene[vardepsexclude] += "toaster_package_dumpdata "

do_package[postfuncs] += "toaster_package_dumpdata "
do_package[vardepsexclude] += "toaster_package_dumpdata "

#do_populate_sdk[postfuncs] += "toaster_artifact_dumpdata "
#do_populate_sdk[vardepsexclude] += "toaster_artifact_dumpdata "

#do_populate_sdk_ext[postfuncs] += "toaster_artifact_dumpdata "
#do_populate_sdk_ext[vardepsexclude] += "toaster_artifact_dumpdata "

