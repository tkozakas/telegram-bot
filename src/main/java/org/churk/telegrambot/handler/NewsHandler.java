package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.news.Article;
import org.churk.telegrambot.service.NewsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class NewsHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final NewsService newsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            return getErrorMessage(chatId, messageId, "Please provide a query (use /news [query]");
        }

        String query = args.getFirst();
        List<Article> articles = newsService.getNewsByCategory(query);

        if (articles.isEmpty()) {
            return getErrorMessage(chatId, messageId, "No news available");
        }
        String text = getNews(articles);
        return getMessage(chatId, text);
    }

    private List<Validable> getMessage(Long chatId, String text) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(text)
                .enableMarkdown(false)
                .build());
    }

    private static String getNews(List<Article> articles) {
        return articles.stream()
                .limit(5)
                .map(article -> article.getTitle() + "\n" + article.getUrl())
                .reduce("", (a, b) -> a + "\n\n" + b);
    }

    private List<Validable> getErrorMessage(Long chatId, Integer messageId, String text) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(text)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.NEWS;
    }
}
