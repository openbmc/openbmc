.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

Using Quilt in Your Workflow
****************************

`Quilt <https://savannah.nongnu.org/projects/quilt>`__ is a powerful tool
that allows you to capture source code changes without having a clean
source tree. This section outlines the typical workflow you can use to
modify source code, test changes, and then preserve the changes in the
form of a patch all using Quilt.

.. note::

   With regard to preserving changes to source files, if you clean a
   recipe or have :ref:`ref-classes-rm-work` enabled, the
   :ref:`devtool workflow <sdk-manual/extensible:using \`\`devtool\`\` in your sdk workflow>`
   as described in the Yocto Project Application Development and the
   Extensible Software Development Kit (eSDK) manual is a safer
   development flow than the flow that uses Quilt.

Follow these general steps:

#. *Find the Source Code:* Temporary source code used by the
   OpenEmbedded build system is kept in the :term:`Build Directory`. See the
   ":ref:`dev-manual/temporary-source-code:finding temporary source code`" section to
   learn how to locate the directory that has the temporary source code for a
   particular package.

#. *Change Your Working Directory:* You need to be in the directory that
   has the temporary source code. That directory is defined by the
   :term:`S` variable.

#. *Create a New Patch:* Before modifying source code, you need to
   create a new patch. To create a new patch file, use ``quilt new`` as
   below::

      $ quilt new my_changes.patch

#. *Notify Quilt and Add Files:* After creating the patch, you need to
   notify Quilt about the files you plan to edit. You notify Quilt by
   adding the files to the patch you just created::

      $ quilt add file1.c file2.c file3.c

#. *Edit the Files:* Make your changes in the source code to the files
   you added to the patch.

#. *Test Your Changes:* Once you have modified the source code, the
   easiest way to test your changes is by calling the :ref:`ref-tasks-compile`
   task as shown in the following example::

      $ bitbake -c compile -f package

   The ``-f`` or ``--force`` option forces the specified task to
   execute. If you find problems with your code, you can just keep
   editing and re-testing iteratively until things work as expected.

   .. note::

      All the modifications you make to the temporary source code disappear
      once you run the :ref:`ref-tasks-clean` or :ref:`ref-tasks-cleanall`
      tasks using BitBake (i.e. ``bitbake -c clean package`` and
      ``bitbake -c cleanall package``). Modifications will also disappear if
      you use the :ref:`ref-classes-rm-work` feature as described in
      the ":ref:`dev-manual/disk-space:conserving disk space during builds`"
      section.

#. *Generate the Patch:* Once your changes work as expected, you need to
   use Quilt to generate the final patch that contains all your
   modifications::

      $ quilt refresh

   At this point, the
   ``my_changes.patch`` file has all your edits made to the ``file1.c``,
   ``file2.c``, and ``file3.c`` files.

   You can find the resulting patch file in the ``patches/``
   subdirectory of the source (:term:`S`) directory.

#. *Copy the Patch File:* For simplicity, copy the patch file into a
   directory named ``files``, which you can create in the same directory
   that holds the recipe (``.bb``) file or the append (``.bbappend``)
   file. Placing the patch here guarantees that the OpenEmbedded build
   system will find the patch. Next, add the patch into the :term:`SRC_URI`
   of the recipe. Here is an example::

      SRC_URI += "file://my_changes.patch"

