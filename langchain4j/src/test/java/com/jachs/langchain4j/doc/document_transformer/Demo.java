package com.jachs.langchain4j.doc.document_transformer;

import java.time.Duration;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ai.docling.serve.api.chunk.response.Document;
import dev.langchain4j.community.data.document.graph.GraphDocument;
import dev.langchain4j.community.data.document.graph.GraphEdge;
import dev.langchain4j.community.data.document.graph.GraphNode;
import dev.langchain4j.community.data.document.transformer.graph.GraphTransformer;
import dev.langchain4j.community.data.document.transformer.graph.LLMGraphTransformer;
import dev.langchain4j.model.openai.OpenAiChatModel;

/***
DocumentTransformer实现可以执行多种文档转换，例如：
清洗：这涉及从Document的文本中去除不必要的噪声，可以节省令牌并减少干扰。
过滤：完全排除特定的Document，使其不参与搜索。
丰富：可以向Document添加附加信息，以潜在地改善搜索结果。
摘要：可以对Document进行摘要，并将其简短摘要存储在Metadata中， 以便稍后包含在每个TextSegment（我们将在下面介绍）中，从而潜在地改善搜索。
等等。
 * @author zhanchaohan
 */
public class Demo {

	
	@Test
	public void t1() {
//		HtmlToTextDocumentTransformer transformer=new HtmlToTextDocumentTransformer();
	}
	
	
}
