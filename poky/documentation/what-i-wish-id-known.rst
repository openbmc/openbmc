.. SPDX-License-Identifier: CC-BY-SA-2.0-UK

=========================================
What I wish I'd known about Yocto Project
=========================================

|

.. note::

   Before reading further, make sure you've taken a look at the
   :yocto_home:`Software Overview</software-overview>` page which presents the
   definitions for many of the terms referenced here. Also, know that some of the
   information here won't make sense now, but as you start developing, it is the
   information you'll want to keep close at hand. These are best known methods for
   working with Yocto Project and they are updated regularly.

Using the Yocto Project is fairly easy, *until something goes wrong*. Without an
understanding of how the build process works, you'll find yourself trying to
troubleshoot "a black box". Here are a few items that new users wished they had
known before embarking on their first build with Yocto Project. Feel free to
contact us with other suggestions.

#. **Use Git, not the tarball download:**
   If you use git the software will be automatically updated with bug updates
   because of how git works. If you download the tarball instead, you will need
   to be responsible for your own updates.

#. **Get to know the layer index:**
   All layers can be found in the :oe_layerindex:`layer index <>`. Layers which
   have applied for Yocto Project Compatible status (structure continuity
   assurance and testing) can be found in the :yocto_home:`Yocto Project Compatible index
   </software-over/layer/>`. Generally check the Compatible layer index first,
   and if you don't find the necessary layer check the general layer index. The
   layer index is an original artifact from the Open Embedded Project. As such,
   that index doesn't have the curating and testing that the Yocto Project
   provides on Yocto Project Compatible layer list, but the latter has fewer
   entries. Know that when you start searching in the layer index that not all
   layers have the same level of maturity, validation, or usability.  Nor do
   searches prioritize displayed results. There is no easy way to help you
   through the process of choosing the best layer to suit your needs.
   Consequently, it is often trial and error, checking the mailing lists, or
   working with other developers through collaboration rooms that can help you
   make good choices.

#. **Use existing BSP layers from silicon vendors when possible:**
   Intel, TI, NXP and others have information on what BSP layers to use with
   their silicon. These layers have names such as "meta-intel" or "meta-ti". Try
   not to build layers from scratch. If you do have custom silicon, use one of
   these layers as a guide or template and familiarize yourself with the
   :doc:`bsp-guide/index`.

#. **Do not put everything into one layer:**
   Use different layers to logically separate information in your build. As an
   example, you could have a BSP layer, a GUI layer, a distro configuration,
   middleware, or an application (e.g. "meta-filesystems", "meta-python",
   "meta-intel", and so forth).  Putting your entire build into one layer limits
   and complicates future customization and reuse.  Isolating information into
   layers, on the other hand, helps keep simplify future customizations and
   reuse.

#. **Never modify the POKY layer. Never. Ever. When you update to the next
   release, you'll lose all of your work. ALL OF IT.**

#. **Don't be fooled by documentation searching results:**
   Yocto Project documentation is always being updated. Unfortunately, when you
   use Google to search for Yocto Project concepts or terms, Google consistently
   searches and retrieves older versions of Yocto Project manuals. For example,
   searching for a particular topic using Google could result in a "hit" on a
   Yocto Project manual that is several releases old. To be sure that you are
   using the most current Yocto Project documentation, use the drop-down menu at
   the top of any of its page.

   Many developers look through the :yocto_docs:`All-in-one 'Mega' Manual </singleindex.html>`
   for a concept or term by doing a search through the whole page.  This manual
   is a concatenation of the core set of Yocto Project manual.  Thus, a simple
   string search using Ctrl-F in this manual produces all the "hits" for a
   desired term or concept.  Once you find the area in which you are
   interested, you can display the actual manual, if desired. It is also
   possible to use the search bar in the menu or in the left navigation pane.

#. **Understand the basic concepts of how the build system works: the workflow:**
   Understanding the Yocto Project workflow is important as it can help you both
   pinpoint where trouble is occurring and how the build is breaking. The
   workflow breaks down into the following steps:

   #. Fetch – get the source code
   #. Extract – unpack the sources
   #. Patch – apply patches for bug fixes and new capability
   #. Configure – set up your environment specifications
   #. Build – compile and link
   #. Install – copy files to target directories
   #. Package – bundle files for installation

   During "fetch", there may be an inability to find code. During "extract",
   there is likely an invalid zip or something similar. In other words, the
   function of a particular part of the workflow gives you an idea of what might
   be going wrong.

   .. image:: figures/yp-how-it-works-new-diagram.png

#. **Know that you can generate a dependency graph and learn how to do it:**
   A dependency graph shows dependencies between recipes, tasks, and targets.
   You can use the "-g" option with BitBake to generate this graph.  When you
   start a build and the build breaks, you could see packages you have no clue
   about or have any idea why the build system has included them.  The
   dependency graph can clarify that confusion.  You can learn more about
   dependency graphs and how to generate them in the
   :ref:`bitbake-user-manual/bitbake-user-manual-intro:generating dependency
   graphs` section in the BitBake User Manual.

#. **Here's how you decode "magic" folder names in tmp/work:**
   The build system fetches, unpacks, preprocesses, and builds. If something
   goes wrong, the build system reports to you directly the path to a folder
   where the temporary (build/tmp) files and packages reside resulting from the
   build.  For a detailed example of this process, see the :yocto_wiki:`example
   </Cookbook:Example:Adding_packages_to_your_OS_image>`. Unfortunately this
   example is on an earlier release of Yocto Project.

   When you perform a build, you can use the "-u" BitBake command-line option to
   specify a user interface viewer into the dependency graph (e.g. knotty,
   ncurses, or taskexp) that helps you understand the build dependencies better.

#. **You can build more than just images:**
   You can build and run a specific task for a specific package (including
   devshell) or even a single recipe. When developers first start using the
   Yocto Project, the instructions found in the
   :doc:`brief-yoctoprojectqs/index` show how to create an image
   and then run or flash that image.  However, you can actually build just a
   single recipe. Thus, if some dependency or recipe isn't working, you can just
   say "bitbake foo" where "foo" is the name for a specific recipe.  As you
   become more advanced using the Yocto Project, and if builds are failing, it
   can be useful to make sure the fetch itself works as desired. Here are some
   valuable links: :ref:`dev-manual/common-tasks:Using a Development
   Shell` for information on how to build and run a specific task using
   devshell. Also, the :ref:`SDK manual shows how to build out a specific recipe
   <sdk-manual/extensible:use \`\`devtool modify\`\` to modify the source of an existing component>`.

#. **An ambiguous definition: Package vs Recipe:**
   A recipe contains instructions the build system uses to create
   packages. Recipes and Packages are the difference between the front end and
   the result of the build process.

   As mentioned, the build system takes the recipe and creates packages from the
   recipe's instructions. The resulting packages are related to the one thing
   the recipe is building but are different parts (packages) of the build
   (i.e. the main package, the doc package, the debug symbols package, the
   separate utilities package, and so forth). The build system splits out the
   packages so that you don't need to install the packages you don't want or
   need, which is advantageous because you are building for small devices when
   developing for embedded and IoT.

#. **You will want to learn about and know what's packaged in rootfs.**

#. **Create your own image recipe:**
   There are a number of ways to create your own image recipe.  We suggest you
   create your own image recipe as opposed to appending an existing recipe.  It
   is trivial and easy to write an image recipe.  Again, do not try appending to
   an existing image recipe. Create your own and do it right from the start.

#. **Finally, here is a list of the basic skills you will need as a systems
   developer. You must be able to:**

   * deal with corporate proxies
   * add a package to an image
   * understand the difference between a recipe and package
   * build a package by itself and why that's useful
   * find out what packages are created by a recipe
   * find out what files are in a package
   * find out what files are in an image
   * add an ssh server to an image (enable transferring of files to target)
   * know the anatomy of a recipe
   * know how to create and use layers
   * find recipes (with the :oe_layerindex:`OpenEmbedded Layer index <>`)
   * understand difference between machine and distro settings
   * find and use the right BSP (machine) for your hardware
   * find examples of distro features and know where to set them
   * understanding the task pipeline and executing individual tasks
   * understand devtool and how it simplifies your workflow
   * improve build speeds with shared downloads and shared state cache
   * generate and understand a dependency graph
   * generate and understand bitbake environment
   * build an Extensible SDK for applications development

#. **Depending on what you primary interests are with the Yocto Project, you
   could consider any of the following reading:**

   * **Look Through the Yocto Project Development Tasks Manual**: This manual
     contains procedural information grouped to help you get set up, work with
     layers, customize images, write new recipes, work with libraries, and use
     QEMU. The information is task-based and spans the breadth of the Yocto
     Project. See the :doc:`/dev-manual/index`.

   * **Look Through the Yocto Project Application Development and the Extensible
     Software Development Kit (eSDK) manual**: This manual describes how to use
     both the standard SDK and the extensible SDK, which are used primarily for
     application development. The :doc:`/sdk-manual/extensible` also provides
     example workflows that use devtool. See the section
     :ref:`sdk-manual/extensible:using \`\`devtool\`\` in your sdk workflow`
     for more information.

   * **Learn About Kernel Development**: If you want to see how to work with the
     kernel and understand Yocto Linux kernels, see the :doc:`/kernel-dev/index`.
     This manual provides information on how to patch the kernel, modify kernel
     recipes, and configure the kernel.

   * **Learn About Board Support Packages (BSPs)**: If you want to learn about
     BSPs, see the :doc:`/bsp-guide/index`. This manual also provides an
     example BSP creation workflow. See the :doc:`/bsp-guide/bsp` section.

   * **Learn About Toaster**: Toaster is a web interface to the Yocto Project's
     OpenEmbedded build system. If you are interested in using this type of
     interface to create images, see the :doc:`/toaster-manual/index`.

   * **Have Available the Yocto Project Reference Manual**: Unlike the rest of
     the Yocto Project manual set, this manual is comprised of material suited
     for reference rather than procedures. You can get build details, a closer
     look at how the pieces of the Yocto Project development environment work
     together, information on various technical details, guidance on migrating
     to a newer Yocto Project release, reference material on the directory
     structure, classes, and tasks. The :doc:`/ref-manual/index` also
     contains a fairly comprehensive glossary of variables used within the Yocto
     Project.

.. include:: /boilerplate.rst
