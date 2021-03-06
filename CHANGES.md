JAMES Extensions Module Changes
===============================

Version 1.2.1 (under development)
---------------------------------

 - ...

Version 1.2 (12/08/2016)
------------------------

 - Upgraded to core module v1.2.

Version 1.1.1 (28/08/2015)
--------------------------

 - Added `NormalizedObjective` wrapper.
 - Added `toString` implementations to several components.
 - Various small improvements.
 
Version 1.1 (11/07/2015)
------------------------

 - Compatible with core module v1.1.
 - Updated `PermutationProblem` to extend the new `GenericProblem`.
   The problem definition includes a default, easily customizable,
   random permutation solution generator.
 - Reordered arguments in `PermutationProblem` constructor.
 - Various improvements.

Version 1.0 (17/06/2015)
------------------------

 - Compatible with core module v1.0.
 - Dedicated customizable random generator per search.
 - Various improvements and minor bug fixes.
 - Moved SearchFactory interface to core module.
 - Moved to SLF4J API 1.7.12.

Version 0.2.1 (02/04/2015)
--------------------------

 - Automated analysis workflow.
 - Improved test coverage.
 - Fixed conflicting logback settings in tests. Settings are now inherited from
   the core module.
 - Minor bugfixes and improvements.

Version 0.2 (12/11/2014)
------------------------

 - Compatible with core module v0.2.
 - Provided specific components for permutation problems.
 - Renamed WeightedMultiObjective to WeightedIndex.
 - Simplified code using functional operations.
 - Moved to SLF4J API 1.7.7.
 - Moved to Java 8.


Version 0.1 (25/06/2014)
------------------------

 - First stable release of the JAMES Extensions Module.
