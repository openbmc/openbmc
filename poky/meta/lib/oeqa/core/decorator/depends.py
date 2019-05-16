#
# Copyright (C) 2016 Intel Corporation
#
# SPDX-License-Identifier: MIT
#

from unittest import SkipTest

from oeqa.core.exception import OEQADependency

from . import OETestDiscover, registerDecorator

def _add_depends(registry, case, depends):
    module_name = case.__module__
    class_name = case.__class__.__name__

    case_id = case.id()

    for depend in depends:
        dparts = depend.split('.')

        if len(dparts) == 1:
            depend_id = ".".join((module_name, class_name, dparts[0]))
        elif len(dparts) == 2:
            depend_id = ".".join((module_name, dparts[0], dparts[1]))
        else:
            depend_id = depend

        if not case_id in registry:
            registry[case_id] = []
        if not depend_id in registry[case_id]:
            registry[case_id].append(depend_id)

def _validate_test_case_depends(cases, depends):
    for case in depends:
        if not case in cases:
            continue
        for dep in depends[case]:
            if not dep in cases:
                raise OEQADependency("TestCase %s depends on %s and isn't available"\
                       ", cases available %s." % (case, dep, str(cases.keys())))

def _order_test_case_by_depends(cases, depends):
    def _dep_resolve(graph, node, resolved, seen):
        seen.append(node)
        for edge in graph[node]:
            if edge not in resolved:
                if edge in seen:
                    raise OEQADependency("Test cases %s and %s have a circular" \
                                       " dependency." % (node, edge))
                _dep_resolve(graph, edge, resolved, seen)
        resolved.append(node)

    dep_graph = {}
    dep_graph['__root__'] = cases.keys()
    for case in cases:
        if case in depends:
            dep_graph[case] = depends[case]
        else:
            dep_graph[case] = []

    cases_ordered = []
    _dep_resolve(dep_graph, '__root__', cases_ordered, [])
    cases_ordered.remove('__root__')

    return [cases[case_id] for case_id in cases_ordered]

def _skipTestDependency(case, depends):
    for dep in depends:
        found = False
        for test, _ in case.tc.results.successes:
            if test.id() == dep:
                found = True
                break
        if not found:
            raise SkipTest("Test case %s depends on %s but it didn't pass/run." \
                        % (case.id(), dep))

@registerDecorator
class OETestDepends(OETestDiscover):
    attrs = ('depends',)

    def bind(self, registry, case):
        super(OETestDepends, self).bind(registry, case)
        if not registry.get('depends'):
            registry['depends'] = {}
        _add_depends(registry['depends'], case, self.depends)

    @staticmethod
    def discover(registry):
        if registry.get('depends'):
            _validate_test_case_depends(registry['cases'], registry['depends'])
            return _order_test_case_by_depends(registry['cases'], registry['depends'])
        else:
            return [registry['cases'][case_id] for case_id in registry['cases']]

    def setUpDecorator(self):
        _skipTestDependency(self.case, self.depends)
