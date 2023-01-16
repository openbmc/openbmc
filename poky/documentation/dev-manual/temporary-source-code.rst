.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Finding Temporary Source Code
*****************************

You might find it helpful during development to modify the temporary
source code used by recipes to build packages. For example, suppose you
are developing a patch and you need to experiment a bit to figure out
your solution. After you have initially built the package, you can
iteratively tweak the source code, which is located in the
:term:`Build Directory`, and then you can force a re-compile and quickly
test your altered code. Once you settle on a solution, you can then preserve
your changes in the form of patches.

During a build, the unpacked temporary source code used by recipes to
build packages is available in the :term:`Build Directory` as defined by the
:term:`S` variable. Below is the default value for the :term:`S` variable as
defined in the ``meta/conf/bitbake.conf`` configuration file in the
:term:`Source Directory`::

   S = "${WORKDIR}/${BP}"

You should be aware that many recipes override the
:term:`S` variable. For example, recipes that fetch their source from Git
usually set :term:`S` to ``${WORKDIR}/git``.

.. note::

   The :term:`BP` represents the base recipe name, which consists of the name
   and version::

           BP = "${BPN}-${PV}"


The path to the work directory for the recipe
(:term:`WORKDIR`) is defined as
follows::

   ${TMPDIR}/work/${MULTIMACH_TARGET_SYS}/${PN}/${EXTENDPE}${PV}-${PR}

The actual directory depends on several things:

-  :term:`TMPDIR`: The top-level build
   output directory.

-  :term:`MULTIMACH_TARGET_SYS`:
   The target system identifier.

-  :term:`PN`: The recipe name.

-  :term:`EXTENDPE`: The epoch --- if
   :term:`PE` is not specified, which is
   usually the case for most recipes, then :term:`EXTENDPE` is blank.

-  :term:`PV`: The recipe version.

-  :term:`PR`: The recipe revision.

As an example, assume a Source Directory top-level folder named
``poky``, a default :term:`Build Directory` at ``poky/build``, and a
``qemux86-poky-linux`` machine target system. Furthermore, suppose your
recipe is named ``foo_1.3.0.bb``. In this case, the work directory the
build system uses to build the package would be as follows::

   poky/build/tmp/work/qemux86-poky-linux/foo/1.3.0-r0

