#
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Toaster Implementation
#
# Copyright (C) 2016 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

from orm.models import Build, Task, Target, Package
from django.db.models import Q, Sum

import toastergui.tables as tables
from toastergui.widgets import ToasterTable
from toastergui.tablefilter import TableFilter
from toastergui.tablefilter import TableFilterActionToggle


class BuildTablesMixin(ToasterTable):
    def get_context_data(self, **kwargs):
        # We need to be explicit about which superclass we're calling here
        # Otherwise the MRO gets in a right mess
        context = ToasterTable.get_context_data(self, **kwargs)
        context['build'] = Build.objects.get(pk=kwargs['build_id'])
        return context


class BuiltPackagesTableBase(tables.PackagesTable):
    """ Table to display all the packages built in a build """
    def __init__(self, *args, **kwargs):
        super(BuiltPackagesTableBase, self).__init__(*args, **kwargs)
        self.title = "Packages built"
        self.default_orderby = "name"

    def setup_queryset(self, *args, **kwargs):
        build = Build.objects.get(pk=kwargs['build_id'])
        self.static_context_extra['build'] = build
        self.static_context_extra['target_name'] = None
        self.queryset = build.package_set.all().exclude(recipe=None)
        self.queryset = self.queryset.order_by(self.default_orderby)

    def setup_columns(self, *args, **kwargs):
        super(BuiltPackagesTableBase, self).setup_columns(*args, **kwargs)

        def pkg_link_template(val):
            """ return the template used for the link with the val as the
            element value i.e. inside the <a></a>"""

            return ('''
                    <a href="
                    {%% url "package_built_detail" extra.build.pk data.pk %%}
                    ">%s</a>
                    ''' % val)

        def recipe_link_template(val):
            return ('''
                    {%% if data.recipe %%}
                    <a href="
                    {%% url "recipe" extra.build.pk data.recipe.pk %%}
                    ">%(value)s</a>
                    {%% else %%}
                    %(value)s
                    {%% endif %%}
                    ''' % {'value': val})

        add_pkg_link_to = 'name'
        add_recipe_link_to = 'recipe__name'

        # Add the recipe and pkg build links to the required columns
        for column in self.columns:
            # Convert to template field style accessors
            tmplv = column['field_name'].replace('__', '.')
            tmplv = "{{data.%s}}" % tmplv

            if column['field_name'] is add_pkg_link_to:
                # Don't overwrite an existing template
                if column['static_data_template']:
                    column['static_data_template'] =\
                        pkg_link_template(column['static_data_template'])
                else:
                    column['static_data_template'] = pkg_link_template(tmplv)

                column['static_data_name'] = column['field_name']

            elif column['field_name'] is add_recipe_link_to:
                # Don't overwrite an existing template
                if column['static_data_template']:
                    column['static_data_template'] =\
                        recipe_link_template(column['static_data_template'])
                else:
                    column['static_data_template'] =\
                        recipe_link_template(tmplv)
                column['static_data_name'] = column['field_name']

        self.add_column(title="Layer",
                        field_name="recipe__layer_version__layer__name",
                        hidden=True,
                        orderable=True)

        layer_branch_template = '''
        {%if not data.recipe.layer_version.layer.local_source_dir %}
        <span class="text-muted">{{data.recipe.layer_version.branch}}</span>
        {% else %}
        <span class="text-muted">Not applicable</span>
        <span class="glyphicon glyphicon-question-sign get-help" data-original-title="" title="The source code of {{data.recipe.layer_version.layer.name}} is not in a Git repository, so there is no branch associated with it"> </span>
        {% endif %}
        '''

        self.add_column(title="Layer branch",
                        field_name="recipe__layer_version__branch",
                        hidden=True,
                        static_data_name="recipe__layer_version__branch",
                        static_data_template=layer_branch_template,
                        orderable=True)

        git_rev_template = '''
        {% if not data.recipe.layer_version.layer.local_source_dir %}
        {% with vcs_ref=data.recipe.layer_version.commit %}
        {% include 'snippets/gitrev_popover.html' %}
        {% endwith %}
        {% else %}
        <span class="text-muted">Not applicable</span>
        <span class="glyphicon glyphicon-question-sign get-help" data-original-title="" title="The source code of {{data.recipe.layer_version.layer.name}} is not in a Git repository, so there is no revision associated with it"> </span>
        {% endif %}
        '''

        self.add_column(title="Layer commit",
                        static_data_name='vcs_ref',
                        static_data_template=git_rev_template,
                        hidden=True)


class BuiltPackagesTable(BuildTablesMixin, BuiltPackagesTableBase):
    """ Show all the packages built for the selected build """
    def __init__(self, *args, **kwargs):
        super(BuiltPackagesTable, self).__init__(*args, **kwargs)
        self.title = "Packages built"
        self.default_orderby = "name"

        self.empty_state =\
            ('<strong>No packages were built.</strong> How did this happen? '
             'Well, BitBake reuses as much stuff as possible. '
             'If all of the packages needed were already built and available '
             'in your build infrastructure, BitBake '
             'will not rebuild any of them. This might be slightly confusing, '
             'but it does make everything faster.')

    def setup_columns(self, *args, **kwargs):
        super(BuiltPackagesTable, self).setup_columns(*args, **kwargs)

        def remove_dep_cols(columns):
            for column in columns:
                # We don't need these fields
                if column['static_data_name'] in ['reverse_dependencies',
                                                  'dependencies']:
                    continue

                yield column

        self.columns = list(remove_dep_cols(self.columns))


class InstalledPackagesTable(BuildTablesMixin, BuiltPackagesTableBase):
    """ Show all packages installed in an image """
    def __init__(self, *args, **kwargs):
        super(InstalledPackagesTable, self).__init__(*args, **kwargs)
        self.title = "Packages Included"
        self.default_orderby = "name"

    def make_package_list(self, target):
        # The database design means that you get the intermediate objects and
        # not package objects like you'd really want so we get them here
        pkgs = target.target_installed_package_set.values_list('package',
                                                               flat=True)
        return Package.objects.filter(pk__in=pkgs)

    def get_context_data(self, **kwargs):
        context = super(InstalledPackagesTable,
                        self).get_context_data(**kwargs)

        target = Target.objects.get(pk=kwargs['target_id'])
        packages = self.make_package_list(target)

        context['packages_sum'] = packages.aggregate(
            Sum('installed_size'))['installed_size__sum']

        context['target'] = target
        return context

    def setup_queryset(self, *args, **kwargs):
        build = Build.objects.get(pk=kwargs['build_id'])
        self.static_context_extra['build'] = build

        target = Target.objects.get(pk=kwargs['target_id'])
        # We send these separately because in the case of image details table
        # we don't have a target just the recipe name as the target
        self.static_context_extra['target_name'] = target.target
        self.static_context_extra['target_id'] = target.pk

        self.static_context_extra['add_links'] = True

        self.queryset = self.make_package_list(target)
        self.queryset = self.queryset.order_by(self.default_orderby)

    def setup_columns(self, *args, **kwargs):
        super(InstalledPackagesTable, self).setup_columns(**kwargs)
        self.add_column(title="Installed size",
                        static_data_name="installed_size",
                        static_data_template="{% load projecttags %}"
                        "{{data.size|filtered_filesizeformat}}",
                        orderable=True,
                        hidden=True)

        # Add the template to show installed name for installed packages
        install_name_tmpl =\
            ('<a href="{% url "package_included_detail" extra.build.pk'
             ' extra.target_id data.pk %}">{{data.name}}</a>'
             '{% if data.installed_name and data.installed_name !='
             ' data.name %}'
             '<span class="text-muted"> as {{data.installed_name}}</span>'
             ' <span class="glyphicon glyphicon-question-sign get-help hover-help"'
             ' title="{{data.name}} was renamed at packaging time and'
             ' was installed in your image as {{data.installed_name}}'
             '"></span>{% endif %} ')

        for column in self.columns:
            if column['static_data_name'] == 'name':
                column['static_data_template'] = install_name_tmpl
                break


class BuiltRecipesTable(BuildTablesMixin):
    """ Table to show the recipes that have been built in this build """

    def __init__(self, *args, **kwargs):
        super(BuiltRecipesTable, self).__init__(*args, **kwargs)
        self.title = "Recipes built"
        self.default_orderby = "name"

    def setup_queryset(self, *args, **kwargs):
        build = Build.objects.get(pk=kwargs['build_id'])
        self.static_context_extra['build'] = build
        self.queryset = build.get_recipes()
        self.queryset = self.queryset.order_by(self.default_orderby)

    def setup_columns(self, *args, **kwargs):
        recipe_name_tmpl =\
            '<a href="{% url "recipe" extra.build.pk data.pk %}">'\
            '{{data.name}}'\
            '</a>'

        recipe_file_tmpl =\
            '{{data.file_path}}'\
            '{% if data.pathflags %}<i>({{data.pathflags}})</i>'\
            '{% endif %}'

        git_branch_template = '''
        {% if data.layer_version.layer.local_source_dir %}
        <span class="text-muted">Not applicable</span>
        <span class="glyphicon glyphicon-question-sign get-help" data-original-title="" title="The source code of {{data.layer_version.layer.name}} is not in a Git repository, so there is no branch associated with it"> </span>
        {% else %}
        <span>{{data.layer_version.branch}}</span>
        {% endif %}
        '''

        git_rev_template = '''
        {% if data.layer_version.layer.local_source_dir %}
        <span class="text-muted">Not applicable</span>
        <span class="glyphicon glyphicon-question-sign get-help" data-original-title="" title="The source code of {{data.layer_version.layer.name}} is not in a Git repository, so there is no commit associated with it"> </span>
        {% else %}
        {% with vcs_ref=data.layer_version.commit %}
        {% include 'snippets/gitrev_popover.html' %}
        {% endwith %}
        {% endif %}
        '''

        depends_on_tmpl = '''
        {% with deps=data.r_dependencies_recipe.all %}
        {% with count=deps|length %}
        {% if count %}
        <a class="btn btn-default" title="
        <a href='{% url "recipe" extra.build.pk data.pk %}#dependencies'>
        {{data.name}}</a> dependencies"
        data-content="<ul class='list-unstyled'>
        {% for dep in deps|dictsort:"depends_on.name"%}
        <li><a href='{% url "recipe" extra.build.pk dep.depends_on.pk %}'>
        {{dep.depends_on.name}}</a></li>
        {% endfor %}
        </ul>">
         {{count}}
        </a>
        {% endif %}{% endwith %}{% endwith %}
        '''

        rev_depends_tmpl = '''
        {% with revs=data.r_dependencies_depends.all %}
        {% with count=revs|length %}
        {% if count %}
        <a class="btn btn-default"
        title="
        <a href='{% url "recipe" extra.build.pk data.pk %}#brought-in-by'>
        {{data.name}}</a> reverse dependencies"
        data-content="<ul class='list-unstyled'>
        {% for dep in revs|dictsort:"recipe.name" %}
        <li>
        <a href='{% url "recipe" extra.build.pk dep.recipe.pk %}'>
        {{dep.recipe.name}}
        </a></li>
        {% endfor %}
        </ul>">
        {{count}}
        </a>
        {% endif %}{% endwith %}{% endwith %}
        '''

        self.add_column(title="Recipe",
                        field_name="name",
                        static_data_name='name',
                        orderable=True,
                        hideable=False,
                        static_data_template=recipe_name_tmpl)

        self.add_column(title="Version",
                        hideable=False,
                        field_name="version")

        self.add_column(title="Dependencies",
                        static_data_name="dependencies",
                        static_data_template=depends_on_tmpl)

        self.add_column(title="Reverse dependencies",
                        static_data_name="revdeps",
                        static_data_template=rev_depends_tmpl,
                        help_text='Recipe build-time reverse dependencies'
                        ' (i.e. the recipes that depend on this recipe)')

        self.add_column(title="Recipe file",
                        field_name="file_path",
                        static_data_name="file_path",
                        static_data_template=recipe_file_tmpl,
                        hidden=True)

        self.add_column(title="Section",
                        field_name="section",
                        orderable=True,
                        hidden=True)

        self.add_column(title="License",
                        field_name="license",
                        help_text='Multiple license names separated by the'
                        ' pipe character indicates a choice between licenses.'
                        ' Multiple license names separated by the ampersand'
                        ' character indicates multiple licenses exist that'
                        ' cover different parts of the source',
                        orderable=True)

        self.add_column(title="Layer",
                        field_name="layer_version__layer__name",
                        orderable=True)

        self.add_column(title="Layer branch",
                        field_name="layer_version__branch",
                        static_data_name="layer_version__branch",
                        static_data_template=git_branch_template,
                        orderable=True,
                        hidden=True)

        self.add_column(title="Layer commit",
                        static_data_name="commit",
                        static_data_template=git_rev_template,
                        hidden=True)


class BuildTasksTable(BuildTablesMixin):
    """ Table to show the tasks that run in this build """

    def __init__(self, *args, **kwargs):
        super(BuildTasksTable, self).__init__(*args, **kwargs)
        self.title = "Tasks"
        self.default_orderby = "order"

        # Toggle these columns on off for Time/CPU usage/Disk I/O tables
        self.toggle_columns = {}

    def setup_queryset(self, *args, **kwargs):
        build = Build.objects.get(pk=kwargs['build_id'])
        self.static_context_extra['build'] = build
        self.queryset = build.task_build.filter(~Q(order=None))
        self.queryset = self.queryset.order_by(self.default_orderby)

    def setup_filters(self, *args, **kwargs):
        # Execution outcome types filter
        executed_outcome = TableFilter(name="execution_outcome",
                                       title="Filter Tasks by 'Executed")

        exec_outcome_action_exec = TableFilterActionToggle(
            "executed",
            "Executed Tasks",
            Q(task_executed=True))

        exec_outcome_action_not_exec = TableFilterActionToggle(
            "not_executed",
            "Not Executed Tasks",
            Q(task_executed=False))

        executed_outcome.add_action(exec_outcome_action_exec)
        executed_outcome.add_action(exec_outcome_action_not_exec)

        # Task outcome types filter
        task_outcome = TableFilter(name="task_outcome",
                                   title="Filter Task by 'Outcome'")

        for outcome_enum, title in Task.TASK_OUTCOME:
            if outcome_enum is Task.OUTCOME_NA:
                continue
            action = TableFilterActionToggle(
                title.replace(" ", "_").lower(),
                "%s Tasks" % title,
                Q(outcome=outcome_enum))

            task_outcome.add_action(action)

        # SSTATE outcome types filter
        sstate_outcome = TableFilter(name="sstate_outcome",
                                     title="Filter Task by 'Cache attempt'")

        for sstate_result_enum, title in Task.SSTATE_RESULT:
            action = TableFilterActionToggle(
                title.replace(" ", "_").lower(),
                "Tasks with '%s' attempts" % title,
                Q(sstate_result=sstate_result_enum))

            sstate_outcome.add_action(action)

        self.add_filter(sstate_outcome)
        self.add_filter(executed_outcome)
        self.add_filter(task_outcome)

    def setup_columns(self, *args, **kwargs):
        self.toggle_columns['order'] = len(self.columns)

        recipe_name_tmpl =\
            '<a href="{% url "recipe" extra.build.pk data.recipe.pk %}">'\
            '{{data.recipe.name}}'\
            '</a>'

        def task_link_tmpl(val):
            return ('<a name="task-{{data.order}}"'
                    'href="{%% url "task" extra.build.pk data.pk %%}">'
                    '%s'
                    '</a>') % str(val)

        self.add_column(title="Order",
                        static_data_name="order",
                        static_data_template='{{data.order}}',
                        hideable=False,
                        orderable=True)

        self.add_column(title="Task",
                        static_data_name="task_name",
                        static_data_template=task_link_tmpl(
                            "{{data.task_name}}"),
                        hideable=False,
                        orderable=True)

        self.add_column(title="Recipe",
                        static_data_name='recipe__name',
                        static_data_template=recipe_name_tmpl,
                        hideable=False,
                        orderable=True)

        self.add_column(title="Recipe version",
                        field_name='recipe__version',
                        hidden=True)

        self.add_column(title="Executed",
                        static_data_name="task_executed",
                        static_data_template='{{data.get_executed_display}}',
                        filter_name='execution_outcome',
                        orderable=True)

        self.static_context_extra['OUTCOME_FAILED'] = Task.OUTCOME_FAILED
        outcome_tmpl = '{{data.outcome_text}}'
        outcome_tmpl = ('%s '
                        '{%% if data.outcome = extra.OUTCOME_FAILED %%}'
                        '<a href="{%% url "build_artifact" extra.build.pk '
                        '          "tasklogfile" data.pk %%}">'
                        ' <span class="glyphicon glyphicon-download-alt'
                        ' get-help" title="Download task log file"></span>'
                        '</a> {%% endif %%}'
                        '<span class="glyphicon glyphicon-question-sign'
                        ' get-help hover-help" style="visibility: hidden;" '
                        'title="{{data.get_outcome_help}}"></span>'
                        ) % outcome_tmpl

        self.add_column(title="Outcome",
                        static_data_name="outcome",
                        static_data_template=outcome_tmpl,
                        filter_name="task_outcome",
                        orderable=True)

        self.toggle_columns['sstate_result'] = len(self.columns)

        self.add_column(title="Cache attempt",
                        static_data_name="sstate_result",
                        static_data_template='{{data.sstate_text}}',
                        filter_name="sstate_outcome",
                        orderable=True)

        self.toggle_columns['elapsed_time'] = len(self.columns)

        self.add_column(
            title="Time (secs)",
            static_data_name="elapsed_time",
            static_data_template='{% load projecttags %}{% load humanize %}'
            '{{data.elapsed_time|format_none_and_zero|floatformat:2}}',
            orderable=True,
            hidden=True)

        self.toggle_columns['cpu_time_sys'] = len(self.columns)

        self.add_column(
            title="System CPU time (secs)",
            static_data_name="cpu_time_system",
            static_data_template='{% load projecttags %}{% load humanize %}'
            '{{data.cpu_time_system|format_none_and_zero|floatformat:2}}',
            hidden=True,
            orderable=True)

        self.toggle_columns['cpu_time_user'] = len(self.columns)

        self.add_column(
            title="User CPU time (secs)",
            static_data_name="cpu_time_user",
            static_data_template='{% load projecttags %}{% load humanize %}'
            '{{data.cpu_time_user|format_none_and_zero|floatformat:2}}',
            hidden=True,
            orderable=True)

        self.toggle_columns['disk_io'] = len(self.columns)

        self.add_column(
            title="Disk I/O (ms)",
            static_data_name="disk_io",
            static_data_template='{% load projecttags %}{% load humanize %}'
            '{{data.disk_io|format_none_and_zero|filtered_filesizeformat}}',
            hidden=True,
            orderable=True)


class BuildTimeTable(BuildTasksTable):
    """ Same as tasks table but the Time column is default displayed"""

    def __init__(self, *args, **kwargs):
        super(BuildTimeTable, self).__init__(*args, **kwargs)
        self.default_orderby = "-elapsed_time"

    def setup_columns(self, *args, **kwargs):
        super(BuildTimeTable, self).setup_columns(**kwargs)

        self.columns[self.toggle_columns['order']]['hidden'] = True
        self.columns[self.toggle_columns['order']]['hideable'] = True
        self.columns[self.toggle_columns['sstate_result']]['hidden'] = True
        self.columns[self.toggle_columns['elapsed_time']]['hidden'] = False


class BuildCPUTimeTable(BuildTasksTable):
    """ Same as tasks table but the CPU usage columns are default displayed"""

    def __init__(self, *args, **kwargs):
        super(BuildCPUTimeTable, self).__init__(*args, **kwargs)
        self.default_orderby = "-cpu_time_system"

    def setup_columns(self, *args, **kwargs):
        super(BuildCPUTimeTable, self).setup_columns(**kwargs)

        self.columns[self.toggle_columns['order']]['hidden'] = True
        self.columns[self.toggle_columns['order']]['hideable'] = True
        self.columns[self.toggle_columns['sstate_result']]['hidden'] = True
        self.columns[self.toggle_columns['cpu_time_sys']]['hidden'] = False
        self.columns[self.toggle_columns['cpu_time_user']]['hidden'] = False


class BuildIOTable(BuildTasksTable):
    """ Same as tasks table but the Disk IO column is default displayed"""

    def __init__(self, *args, **kwargs):
        super(BuildIOTable, self).__init__(*args, **kwargs)
        self.default_orderby = "-disk_io"

    def setup_columns(self, *args, **kwargs):
        super(BuildIOTable, self).setup_columns(**kwargs)

        self.columns[self.toggle_columns['order']]['hidden'] = True
        self.columns[self.toggle_columns['order']]['hideable'] = True
        self.columns[self.toggle_columns['sstate_result']]['hidden'] = True
        self.columns[self.toggle_columns['disk_io']]['hidden'] = False
