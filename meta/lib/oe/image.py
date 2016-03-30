from oe.utils import execute_pre_post_process
import os
import subprocess
import multiprocessing


def generate_image(arg):
    (type, subimages, create_img_cmd, sprefix) = arg

    bb.note("Running image creation script for %s: %s ..." %
            (type, create_img_cmd))

    try:
        output = subprocess.check_output(create_img_cmd,
                                         stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError as e:
        return("Error: The image creation script '%s' returned %d:\n%s" %
               (e.cmd, e.returncode, e.output))

    bb.note("Script output:\n%s" % output)

    return None


"""
This class will help compute IMAGE_FSTYPE dependencies and group them in batches
that can be executed in parallel.

The next example is for illustration purposes, highly unlikely to happen in real life.
It's just one of the test cases I used to test the algorithm:

For:
IMAGE_FSTYPES = "i1 i2 i3 i4 i5"
IMAGE_TYPEDEP_i4 = "i2"
IMAGE_TYPEDEP_i5 = "i6 i4"
IMAGE_TYPEDEP_i6 = "i7"
IMAGE_TYPEDEP_i7 = "i2"

We get the following list of batches that can be executed in parallel, having the
dependencies satisfied:

[['i1', 'i3', 'i2'], ['i4', 'i7'], ['i6'], ['i5']]
"""
class ImageDepGraph(object):
    def __init__(self, d):
        self.d = d
        self.graph = dict()
        self.deps_array = dict()

    def _construct_dep_graph(self, image_fstypes):
        graph = dict()

        def add_node(node):
            base_type = self._image_base_type(node)
            deps = (self.d.getVar('IMAGE_TYPEDEP_' + node, True) or "")
            base_deps = (self.d.getVar('IMAGE_TYPEDEP_' + base_type, True) or "")

            graph[node] = ""
            for dep in deps.split() + base_deps.split():
                if not dep in graph[node]:
                    if graph[node] != "":
                        graph[node] += " "
                    graph[node] += dep

                if not dep in graph:
                    add_node(dep)

        for fstype in image_fstypes:
            add_node(fstype)

        return graph

    def _clean_graph(self):
        # Live and VMDK/VDI images will be processed via inheriting
        # bbclass and does not get processed here. Remove them from the fstypes
        # graph. Their dependencies are already added, so no worries here.
        remove_list = (self.d.getVar('IMAGE_TYPES_MASKED', True) or "").split()

        for item in remove_list:
            self.graph.pop(item, None)

    def _image_base_type(self, type):
        ctypes = self.d.getVar('COMPRESSIONTYPES', True).split()
        if type in ["vmdk", "vdi", "qcow2", "live", "iso", "hddimg"]:
            type = "ext4"
        basetype = type
        for ctype in ctypes:
            if type.endswith("." + ctype):
                basetype = type[:-len("." + ctype)]
                break

        return basetype

    def _compute_dependencies(self):
        """
        returns dict object of nodes with [no_of_depends_on, no_of_depended_by]
        for each node
        """
        deps_array = dict()
        for node in self.graph:
            deps_array[node] = [0, 0]

        for node in self.graph:
            deps = self.graph[node].split()
            deps_array[node][0] += len(deps)
            for dep in deps:
                deps_array[dep][1] += 1

        return deps_array

    def _sort_graph(self):
        sorted_list = []
        group = []
        for node in self.graph:
            if node not in self.deps_array:
                continue

            depends_on = self.deps_array[node][0]

            if depends_on == 0:
                group.append(node)

        if len(group) == 0 and len(self.deps_array) != 0:
            bb.fatal("possible fstype circular dependency...")

        sorted_list.append(group)

        # remove added nodes from deps_array
        for item in group:
            for node in self.graph:
                if item in self.graph[node].split():
                    self.deps_array[node][0] -= 1

            self.deps_array.pop(item, None)

        if len(self.deps_array):
            # recursive call, to find the next group
            sorted_list += self._sort_graph()

        return sorted_list

    def group_fstypes(self, image_fstypes):
        self.graph = self._construct_dep_graph(image_fstypes)

        self._clean_graph()

        self.deps_array = self._compute_dependencies()

        alltypes = [node for node in self.graph]

        return (alltypes, self._sort_graph())


class Image(ImageDepGraph):
    def __init__(self, d):
        self.d = d

        super(Image, self).__init__(d)

    def _get_rootfs_size(self):
        """compute the rootfs size"""
        rootfs_alignment = int(self.d.getVar('IMAGE_ROOTFS_ALIGNMENT', True))
        overhead_factor = float(self.d.getVar('IMAGE_OVERHEAD_FACTOR', True))
        rootfs_req_size = int(self.d.getVar('IMAGE_ROOTFS_SIZE', True))
        rootfs_extra_space = eval(self.d.getVar('IMAGE_ROOTFS_EXTRA_SPACE', True))
        rootfs_maxsize = self.d.getVar('IMAGE_ROOTFS_MAXSIZE', True)

        output = subprocess.check_output(['du', '-ks',
                                          self.d.getVar('IMAGE_ROOTFS', True)])
        size_kb = int(output.split()[0])
        base_size = size_kb * overhead_factor
        base_size = (base_size, rootfs_req_size)[base_size < rootfs_req_size] + \
            rootfs_extra_space

        if base_size != int(base_size):
            base_size = int(base_size + 1)
        else:
            base_size = int(base_size)

        base_size += rootfs_alignment - 1
        base_size -= base_size % rootfs_alignment

        # Check the rootfs size against IMAGE_ROOTFS_MAXSIZE (if set)
        if rootfs_maxsize:
            rootfs_maxsize_int = int(rootfs_maxsize)
            if base_size > rootfs_maxsize_int:
                bb.fatal("The rootfs size %d(K) overrides the max size %d(K)" % \
                    (base_size, rootfs_maxsize_int))

        return base_size

    def _create_symlinks(self, subimages):
        """create symlinks to the newly created image"""
        deploy_dir = self.d.getVar('DEPLOY_DIR_IMAGE', True)
        img_name = self.d.getVar('IMAGE_NAME', True)
        link_name = self.d.getVar('IMAGE_LINK_NAME', True)
        manifest_name = self.d.getVar('IMAGE_MANIFEST', True)

        os.chdir(deploy_dir)

        if link_name:
            for type in subimages:
                if os.path.exists(img_name + ".rootfs." + type):
                    dst = link_name + "." + type
                    src = img_name + ".rootfs." + type
                    bb.note("Creating symlink: %s -> %s" % (dst, src))
                    os.symlink(src, dst)

            if manifest_name is not None and \
                    os.path.exists(manifest_name) and \
                    not os.path.exists(link_name + ".manifest"):
                os.symlink(os.path.basename(manifest_name),
                           link_name + ".manifest")

    def _remove_old_symlinks(self):
        """remove the symlinks to old binaries"""

        if self.d.getVar('IMAGE_LINK_NAME', True):
            deploy_dir = self.d.getVar('DEPLOY_DIR_IMAGE', True)
            for img in os.listdir(deploy_dir):
                if img.find(self.d.getVar('IMAGE_LINK_NAME', True)) == 0:
                    img = os.path.join(deploy_dir, img)
                    if os.path.islink(img):
                        if self.d.getVar('RM_OLD_IMAGE', True) == "1" and \
                                os.path.exists(os.path.realpath(img)):
                            os.remove(os.path.realpath(img))

                        os.remove(img)

    """
    This function will just filter out the compressed image types from the
    fstype groups returning a (filtered_fstype_groups, cimages) tuple.
    """
    def _filter_out_commpressed(self, fstype_groups):
        ctypes = self.d.getVar('COMPRESSIONTYPES', True).split()
        cimages = {}

        filtered_groups = []
        for group in fstype_groups:
            filtered_group = []
            for type in group:
                basetype = None
                for ctype in ctypes:
                    if type.endswith("." + ctype):
                        basetype = type[:-len("." + ctype)]
                        if basetype not in filtered_group:
                            filtered_group.append(basetype)
                        if basetype not in cimages:
                            cimages[basetype] = []
                        if ctype not in cimages[basetype]:
                            cimages[basetype].append(ctype)
                        break
                if not basetype and type not in filtered_group:
                    filtered_group.append(type)

            filtered_groups.append(filtered_group)

        return (filtered_groups, cimages)

    def _get_image_types(self):
        """returns a (types, cimages) tuple"""

        alltypes, fstype_groups = self.group_fstypes(self.d.getVar('IMAGE_FSTYPES', True).split())

        filtered_groups, cimages = self._filter_out_commpressed(fstype_groups)

        return (alltypes, filtered_groups, cimages)

    def _write_script(self, type, cmds, sprefix=""):
        tempdir = self.d.getVar('T', True)
        script_name = os.path.join(tempdir, sprefix + "create_image." + type)
        rootfs_size = self._get_rootfs_size()

        self.d.setVar('img_creation_func', '\n'.join(cmds))
        self.d.setVarFlag('img_creation_func', 'func', 1)
        self.d.setVarFlag('img_creation_func', 'fakeroot', 1)
        self.d.setVar('ROOTFS_SIZE', str(rootfs_size))

        with open(script_name, "w+") as script:
            script.write("%s" % bb.build.shell_trap_code())
            script.write("export ROOTFS_SIZE=%d\n" % rootfs_size)
            bb.data.emit_func('img_creation_func', script, self.d)
            script.write("img_creation_func\n")

        os.chmod(script_name, 0775)

        return script_name

    def _get_imagecmds(self, sprefix=""):
        old_overrides = self.d.getVar('OVERRIDES', 0)

        alltypes, fstype_groups, cimages = self._get_image_types()

        image_cmd_groups = []

        bb.note("The image creation groups are: %s" % str(fstype_groups))
        for fstype_group in fstype_groups:
            image_cmds = []
            for type in fstype_group:
                cmds = []
                subimages = []

                localdata = bb.data.createCopy(self.d)
                localdata.setVar('OVERRIDES', '%s:%s' % (type, old_overrides))
                bb.data.update_data(localdata)
                localdata.setVar('type', type)

                image_cmd = localdata.getVar("IMAGE_CMD", True)
                if image_cmd:
                    cmds.append("\t" + image_cmd)
                else:
                    bb.fatal("No IMAGE_CMD defined for IMAGE_FSTYPES entry '%s' - possibly invalid type name or missing support class" % type)
                cmds.append(localdata.expand("\tcd ${DEPLOY_DIR_IMAGE}"))

                if type in cimages:
                    for ctype in cimages[type]:
                        cmds.append("\t" + localdata.getVar("COMPRESS_CMD_" + ctype, True))
                        subimages.append(type + "." + ctype)

                if type not in alltypes:
                    cmds.append(localdata.expand("\trm ${IMAGE_NAME}.rootfs.${type}"))
                else:
                    subimages.append(type)

                script_name = self._write_script(type, cmds, sprefix)

                image_cmds.append((type, subimages, script_name, sprefix))

            image_cmd_groups.append(image_cmds)

        return image_cmd_groups

    def _write_wic_env(self):
        """
        Write environment variables used by wic
        to tmp/sysroots/<machine>/imgdata/<image>.env
        """
        stdir = self.d.getVar('STAGING_DIR_TARGET', True)
        outdir = os.path.join(stdir, 'imgdata')
        if not os.path.exists(outdir):
            os.makedirs(outdir)
        basename = self.d.getVar('IMAGE_BASENAME', True)
        with open(os.path.join(outdir, basename) + '.env', 'w') as envf:
            for var in self.d.getVar('WICVARS', True).split():
                value = self.d.getVar(var, True)
                if value:
                    envf.write('%s="%s"\n' % (var, value.strip()))

    def create(self):
        bb.note("###### Generate images #######")
        pre_process_cmds = self.d.getVar("IMAGE_PREPROCESS_COMMAND", True)
        post_process_cmds = self.d.getVar("IMAGE_POSTPROCESS_COMMAND", True)

        execute_pre_post_process(self.d, pre_process_cmds)

        self._remove_old_symlinks()

        image_cmd_groups = self._get_imagecmds()

        # Process the debug filesystem...
        debugfs_d = bb.data.createCopy(self.d)
        if self.d.getVar('IMAGE_GEN_DEBUGFS', True) == "1":
            bb.note("Processing debugfs image(s) ...")
            orig_d = self.d
            self.d = debugfs_d

            self.d.setVar('IMAGE_ROOTFS', orig_d.getVar('IMAGE_ROOTFS', True) + '-dbg')
            self.d.setVar('IMAGE_NAME', orig_d.getVar('IMAGE_NAME', True) + '-dbg')
            self.d.setVar('IMAGE_LINK_NAME', orig_d.getVar('IMAGE_LINK_NAME', True) + '-dbg')

            debugfs_image_fstypes = orig_d.getVar('IMAGE_FSTYPES_DEBUGFS', True)
            if debugfs_image_fstypes:
                self.d.setVar('IMAGE_FSTYPES', orig_d.getVar('IMAGE_FSTYPES_DEBUGFS', True))

            self._remove_old_symlinks()

            image_cmd_groups += self._get_imagecmds("debugfs.")

            self.d = orig_d

        self._write_wic_env()

        for image_cmds in image_cmd_groups:
            # create the images in parallel
            nproc = multiprocessing.cpu_count()
            pool = bb.utils.multiprocessingpool(nproc)
            results = list(pool.imap(generate_image, image_cmds))
            pool.close()
            pool.join()

            for result in results:
                if result is not None:
                    bb.fatal(result)

            for image_type, subimages, script, sprefix in image_cmds:
                if sprefix == 'debugfs.':
                    bb.note("Creating symlinks for %s debugfs image ..." % image_type)
                    orig_d = self.d
                    self.d = debugfs_d
                    self._create_symlinks(subimages)
                    self.d = orig_d
                else:
                    bb.note("Creating symlinks for %s image ..." % image_type)
                    self._create_symlinks(subimages)

        execute_pre_post_process(self.d, post_process_cmds)


def create_image(d):
    Image(d).create()

if __name__ == "__main__":
    """
    Image creation can be called independent from bitbake environment.
    """
    """
    TBD
    """
