package org.docear.plugin.core.features;

import java.net.URI;

import org.freeplane.core.extension.IExtension;

public class DocearMapModelExtension implements IExtension {
	
	public final static String MAP_ID_ATTRIBUTE = "dcr_id";
	public final static String MAP_URI_ATTRIBUTE = "dcr_uri";
	
	String version;
	DocearMapType type;
	String mapId;
	URI uri;
	
	public DocearMapType getType() {
		return type;
	}

	public void setType(DocearMapType type) {
		this.type = type;
	}
	
	public void setType(String type) {
		if(type.equalsIgnoreCase("incoming")){
			this.type = DocearMapType.incoming;
		}
		else if(type.equalsIgnoreCase("my_publications")){
			this.type = DocearMapType.my_publications;
		}
		else if(type.equalsIgnoreCase("literature_annotations")){
			this.type = DocearMapType.literature_annotations;
		}		
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String mapExtensionVersion) {
		this.version = mapExtensionVersion;
	}
	
	public enum DocearMapType{
		incoming, my_publications, literature_annotations
	}

	public String getMapId() {
		return mapId;
	}

	public void setMapId(String mapId) {
		this.mapId = mapId;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
	
	
}