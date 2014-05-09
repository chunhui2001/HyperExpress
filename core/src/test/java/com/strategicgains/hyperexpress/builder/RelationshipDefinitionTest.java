package com.strategicgains.hyperexpress.builder;

import static com.strategicgains.hyperexpress.RelTypes.SELF;
import static com.strategicgains.hyperexpress.RelTypes.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;

import com.strategicgains.hyperexpress.domain.Blog;
import com.strategicgains.hyperexpress.domain.Comment;
import com.strategicgains.hyperexpress.domain.Entry;
import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.Namespace;

public class RelationshipDefinitionTest
{
	@Test
	public void testWithNamespaces()
	throws Exception
	{
		RelationshipDefinition rdef = new RelationshipDefinition()
			.addNamespaces(
				new Namespace("ea", "http://namespaces.example.com/{rel}"),
				new Namespace("blog", "http://namespaces.example.com/{rel}")
			)

			.forCollectionOf(Blog.class)
				.rel(SELF, "/blogs")

			.forClass(Blog.class)
				.rel("ea:author", "/pi/users/{userId}")
				.rel("blog:entries", "/blogs/{blogId}/entries")
				.rel("self", "/blogs/{blogId}")

			.forCollectionOf(Entry.class)
				.rel(SELF, "/blogs/{blogId}/entries")
				.rel(UP, "/blogs/{blogId}")

			.forClass(Entry.class)
				.rel(SELF, "/blogs/{blogId}/entries/{entryId}")
				.rel("blog:comments", "/blog/{blogId}/entries/{entryId}/comments")
				.rel(UP, "/blogs/{blogId}/entries")

			.forCollectionOf(Comment.class)
				.rel(SELF, "/blogs/{blogId}/entries/{entryId}/comments")
				.rel(UP, "/blogs/{blogId}/entries/{entryId}")
					.title("The parent blog entry")

			.forClass(Comment.class)
				.rel(SELF, "/blogs/{blogId}/entries/{entryId}/comments/{commentId}")
					.title("This very comment")
				.rel(UP, "/blogs/{blogId}/entries/{entryId}")
					.title("The parent blog entry")
				.rel("ea:author", "/pi/users/{userId}")
					.title("The comment author");

		verifyNamespacesExist(rdef.getNamespaces().values());

		Collection<Link> links = rdef.getLinkTemplates(Blog.class).values();
		assertNotNull(links);
		assertEquals(3, links.size());

		links = rdef.getCollectionLinkTemplates(Blog.class).values();
		assertNotNull(links);
		assertEquals(1, links.size());
		Link link = links.iterator().next();
		assertEquals(SELF, link.getRel());
		assertEquals("/blogs", link.getHref());

		links = rdef.getLinkTemplates(Entry.class).values();
		assertNotNull(links);
		assertEquals(3, links.size());

		links = rdef.getLinkTemplates(Comment.class).values();
		assertNotNull(links);
		assertEquals(3, links.size());
	}

	private void verifyNamespacesExist(Collection<Namespace> namespaces)
	throws Exception
    {
		assertNotNull(namespaces);
		assertEquals(2, namespaces.size());

		boolean eaChecked = false;
		boolean blogChecked = false;

		for (Namespace namespace : namespaces)
		{
			if ("ea".equals(namespace.name()))
			{
				if (eaChecked) throw new IllegalStateException("Namespace 'ea' already checked.");
				eaChecked = true;
				assertEquals("http://namespaces.example.com/{rel}", namespace.href());
			}

			if ("blog".equals(namespace.name()))
			{
				if (blogChecked) throw new IllegalStateException("Namespace 'blog' already checked.");
				blogChecked = true;
				assertEquals("http://namespaces.example.com/{rel}", namespace.href());
			}
		}

		if (!eaChecked) throw new IllegalStateException("Namespace 'ea' not in returned namespaces");
		if (!blogChecked) throw new IllegalStateException("Namespace 'blog' not in returned namespaces");
    }

	@Test
	public void testWithoutNamespaces()
	{
		RelationshipDefinition rdef = new RelationshipDefinition()
			.forCollectionOf(Blog.class)
				.rel(SELF, "/blogs")
	
			.forClass(Blog.class)
				.rel("ea:author", "/pi/users/{userId}")
				.rel("blog:entries", "/blogs/{blogId}/entries")
				.rel("self", "/blogs/{blogId}")
	
			.forCollectionOf(Entry.class)
				.rel(SELF, "/blogs/{blogId}/entries")
				.rel(UP, "/blogs/{blogId}")
	
			.forClass(Entry.class)
				.rel(SELF, "/blogs/{blogId}/entries/{entryId}")
				.rel("blog:comments", "/blog/{blogId}/entries/{entryId}/comments")
				.rel(UP, "/blogs/{blogId}/entries")
	
			.forCollectionOf(Comment.class)
				.rel(SELF, "/blogs/{blogId}/entries/{entryId}/comments")
				.rel(UP, "/blogs/{blogId}/entries/{entryId}")
					.title("The parent blog entry")
	
			.forClass(Comment.class)
				.rel(SELF, "/blogs/{blogId}/entries/{entryId}/comments/{commentId}")
				.rel(UP, "/blogs/{blogId}/entries/{entryId}")
					.title("The parent blog entry")
				.rel("ea:author", "/pi/users/{userId}");

		Collection<Link> links = rdef.getLinkTemplates(Blog.class).values();
		assertNotNull(links);
		assertEquals(3, links.size());

		links = rdef.getLinkTemplates(Entry.class).values();
		assertNotNull(links);
		assertEquals(3, links.size());

		links = rdef.getLinkTemplates(Comment.class).values();
		assertNotNull(links);
		assertEquals(3, links.size());
	}
}