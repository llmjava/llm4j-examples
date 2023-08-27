# news-article-recommender

News article recommendation with Google PaLM and Elastisearch


Tags extraction:

```
String prompt = "Given a news article, this program returns the list tags containing keywords of that article." + "\n"
                + "Article: japanese banking battle at an end japan s sumitomo mitsui financial has withdrawn its takeover offer for rival bank ufj holdings  enabling the latter to merge with mitsubishi tokyo.  sumitomo bosses told counterparts at ufj of its decision on friday  clearing the way for it to conclude a 3 trillion" + "\n"
                + "Tags: sumitomo mitsui financial, ufj holdings, mitsubishi tokyo, japanese banking" + "\n"
                + "--" + "\n"
                + "Article: france starts digital terrestrial france has become the last big european country to launch a digital terrestrial tv (dtt) service.  initially  more than a third of the population will be able to receive 14 free-to-air channels. despite the long wait for a french dtt roll-out" + "\n"
                + "Tags: france, digital terrestrial" + "\n"
                + "--" + "\n"
                + "Article: apple laptop is  greatest gadget  the apple powerbook 100 has been chosen as the greatest gadget of all time  by us magazine mobile pc.  the 1991 laptop was chosen because it was one of the first  lightweight  portable computers and helped define the layout of all future notebook pcs." + "\n"
                + "Tags: apple, apple powerbook 100, laptop" + "\n"
                + "--" + "\n"
                + "Article: " + article.text + "" + "\n"
                + "Tags:";
String rawTags = llm.process(prompt);
```

Title: Desailly backs Blues revenge trip, tags: [chelsea, barcelona]
