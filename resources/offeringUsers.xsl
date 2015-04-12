<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml"></xsl:output>
	<xsl:template match="/">
		<graphml xmlns="http://graphml.graphdrawing.org/xmlns"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns
     http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">

			<key id="username" for="node" attr.name="username" attr.type="string" />
			<key id="name" for="node" attr.name="name" attr.type="string" />
			<key id="balance" for="node" attr.name="balance" attr.type="int" />

			<graph id="G" edgedefault="undirected">

				<xsl:apply-templates select="//user[@isOffering='y']" />

			</graph>
		</graphml>
	</xsl:template>

	<xsl:template match="user">
		<node id="{username}">
			<data id="username">
				<xsl:value-of select="username"></xsl:value-of>
			</data>
			<data id="name">
				<xsl:value-of select="name"></xsl:value-of>
			</data>
			<data id="balance">
				<xsl:value-of select="balance"></xsl:value-of>
			</data>
		</node>
	</xsl:template>
</xsl:stylesheet>