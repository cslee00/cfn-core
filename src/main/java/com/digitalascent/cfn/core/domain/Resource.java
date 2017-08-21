package com.digitalascent.cfn.core.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class Resource extends CfnObject {
    private final String resourceName;
    private static final Pattern LOGICAL_ID_REGEX = Pattern.compile("[a-zA-Z0-9]+");

    public Resource(String name, String type) {
        super(type, name);
        checkArgument(!isNullOrEmpty(type), "type is required to be non-null & not empty: %s", type);
        setProperty("type", type);
        this.resourceName = name;
        checkArgument(LOGICAL_ID_REGEX.matcher(name).matches(),
                "Invalid resource logical ID '%s'; must be alphanumeric [A-Za-z0-9]: http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/resources-section-structure.html", name);
        checkArgument(name.length() <= 255, "Invalid resource LogicalID '%s'; length must be <= 255", name);
    }

    public static Resource create(String name, String type) {
        Resource res = new Resource(name, type);
        res.setProperty("properties", new CfnObject(type, "properties"));
        return res;
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    public void tag(final String key, final String value) {
        CfnObject properties = (CfnObject) getProperty("properties");
        Map tags = (Map) properties.getProperty("tags");
        if (tags == null) {
            tags = new LinkedHashMap<>();
            properties.setProperty("tags", tags);
        }

        getLogger().info( "{}: tagging with {}={}", resourceName, key, value );
        tags.put(key, value);
    }

    public String getResourceName() {
        return resourceName;
    }
}
