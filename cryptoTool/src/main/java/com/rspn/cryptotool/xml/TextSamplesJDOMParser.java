package com.rspn.cryptotool.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import android.content.Context;
import android.util.Log;

import com.rspn.cryptotool.R;
import com.rspn.cryptotool.model.*;
import com.rspn.cryptotool.utils.CTUtils;

public class TextSamplesJDOMParser {

	private static final String TEXT_TAG = "textSample";
	private static final String TEXT_TITLE = "textSampleTitle";
	private static final String TEXT_TYPE = "textSampleType";
	private static final String TEXT_CONTENT = "content";
	private static final String TEXT_DELETABLE = "deletable";

	

	public List<Text> parseXML(Context context) {

		InputStream stream = context.getResources().openRawResource(R.raw.textsamples);
		SAXBuilder builder = new SAXBuilder();
		List<Text> texts = new ArrayList<Text>();

		try {

			Document document = (Document) builder.build(stream);
			org.jdom2.Element rootNode = document.getRootElement();
			List<org.jdom2.Element> list = rootNode.getChildren(TEXT_TAG);

			for (Element node : list) {//loop through all we have in memory
				Text text = new Text();
				//the node.*Text are jdom functions dont confuse with  Text class
				text.setTitle(node.getChildText(TEXT_TITLE));
				text.setType(node.getChildText(TEXT_TYPE));
				text.setContent(node.getChildText(TEXT_CONTENT));
				text.setDeletable(Integer.parseInt(node.getChildText(TEXT_DELETABLE)));
				texts.add(text);
			}

		} catch (IOException e) {
			Log.i(CTUtils.TAG, e.getMessage());
		} catch (JDOMException e) {
			Log.i(CTUtils.TAG, e.getMessage());
		}
		return texts;
	}

}
