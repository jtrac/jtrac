<?xml version="1.0" ?>

<!-- 
    
    This is the XSL HTML configuration file for the Spring
    documentation.  It is adapted from the one used by the Spring Framework team.
    We thank and credit the Spring Framework development and documentation team.
    
    http://springframework.cvs.sourceforge.net/springframework/spring/docs/reference/styles/
    
    The Spring team in turn credits Christian Bauer of the Hibernate project
    team for writing the original stylesheet upon which this one
    is based.
    
-->

<!DOCTYPE xsl:stylesheet [
    <!ENTITY db_xsl_path        "../lib/docbook-xsl/">
    <!ENTITY callout_gfx_path   "../images/callouts/">
    <!ENTITY admon_gfx_path     "../images/admons/">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="http://www.w3.org/TR/xhtml1/transitional"
    exclude-result-prefixes="#default">
    <xsl:import href="&db_xsl_path;/html/chunk.xsl" />
    <!--###################################################
                     HTML Settings
    ################################################### -->
    <xsl:param name="chunk.section.depth">'5'</xsl:param>
    <xsl:param name="use.id.as.filename">'1'</xsl:param>
    <xsl:param name="html.stylesheet">../styles/html.css</xsl:param>
    <!-- These extensions are required for table printing and other stuff -->
    <xsl:param name="use.extensions">1</xsl:param>
    <xsl:param name="tablecolumns.extension">0</xsl:param>
    <xsl:param name="callout.extensions">1</xsl:param>
    <xsl:param name="graphicsize.extension">0</xsl:param>
    <!--###################################################
                      Table Of Contents
    ################################################### -->
    <!-- Generate the TOCs for named components only -->
    <xsl:param name="generate.toc">
        book   toc
    </xsl:param>
    <!-- Show only Sections up to level 3 in the TOCs -->
    <xsl:param name="toc.section.depth">3</xsl:param>
    <!--###################################################
                         Labels
    ################################################### -->
    <!-- Label Chapters and Sections (numbering) -->
    <xsl:param name="chapter.autolabel">1</xsl:param>
    <xsl:param name="section.autolabel" select="1" />
    <xsl:param name="section.label.includes.component.label" select="1" />
    <!--###################################################
                         Callouts
    ################################################### -->
    <!-- Use images for callouts instead of (1) (2) (3) -->
    <xsl:param name="callout.graphics">1</xsl:param>
    <xsl:param name="callout.graphics.path">&callout_gfx_path;</xsl:param>
    <!-- Place callout marks at this column in annotated areas -->
    <xsl:param name="callout.defaultcolumn">90</xsl:param>
    <!--###################################################
                       Admonitions
    ################################################### -->
    <!-- Use nice graphics for admonitions -->
    <xsl:param name="admon.graphics">'1'</xsl:param>
    <xsl:param name="admon.graphics.path">&admon_gfx_path;</xsl:param>
    <!--###################################################
                          Misc
    ################################################### -->
    <!-- Placement of titles -->
    <xsl:param name="formal.title.placement">
        figure after
        example before
        equation before
        table before
        procedure before
    </xsl:param>
    <xsl:template match="author" mode="titlepage.mode">
        <xsl:if test="name(preceding-sibling::*[1]) = 'author'">
            <xsl:text>, </xsl:text>
        </xsl:if>
        <span class="{name(.)}">
            <xsl:call-template name="person.name" />
            <xsl:apply-templates mode="titlepage.mode" select="./contrib" />
            <xsl:apply-templates mode="titlepage.mode" select="./affiliation" />
        </span>
    </xsl:template>
    <xsl:template match="authorgroup" mode="titlepage.mode">
        <div class="{name(.)}">
            <h2>Authors</h2>
            <p/>
            <xsl:apply-templates mode="titlepage.mode" />
        </div>
    </xsl:template>
<!--###################################################
                     Headers and Footers
    ################################################### --> 
    
    <!-- header banner -->
    <xsl:template name="user.header.navigation">
        <div class="navheader">
            <table width="100%" summary="Navigation footer">
                <tr>
                    <td>
                        <a href="http://jtrac.info/">
                            <img style="border:none" src="../images/jtrac-logo.png"/>
                        </a>
                    </td>                
                    <td align="right">
                        <a href="http://sourceforge.net">
                            <img style="border:none" src="http://sflogo.sourceforge.net/sflogo.php?group_id=162983&amp;type=7"/>
                        </a>
                    </td>
                </tr>
            </table>
        </div>    
    </xsl:template> 
    
    <!-- no header navigation -->
    <xsl:template name="header.navigation"/>
    <xsl:param name="navig.showtitles">1</xsl:param>

    <!-- no footer -->
    <xsl:template name="user.footer.navigation"/>

</xsl:stylesheet>
