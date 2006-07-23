We are using the DocBook XSL distribution for HTML and PDF
generation.  This approach is adapted from what the Spring
Framework team is using.

We thank and credit the Spring development and documentation team.
who in turn credit the Hibernate team for the concept and initial
implementation.

More information about how the Spring Framework team generates
documentation can be found in the readme.txt file at this location:

http://springframework.cvs.sourceforge.net/springframework/spring/docs/reference/

To generate documentation, you need to have the DocBook 
libraries.  The Spring team has created an easy to use distribution
which can be downloaded here:

http://static.springframework.org/spring/files/docbook-reference-libs.zip.

Create a lib directory in the "doc" directory and unzip the zip there.
Then, the Ant targets in the "doc/build.xml" file should work.
