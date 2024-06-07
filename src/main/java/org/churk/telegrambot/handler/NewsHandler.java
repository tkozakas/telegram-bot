package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Article;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.NewsService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NewsHandler extends Handler {
    private final NewsService newsService;

    private static String getNews(List<Article> articles) {
        return articles.stream()
                .limit(3)
                .map(article -> article.getTitle() + "\n" + article.getUrl())
                .reduce("", (a, b) -> a + "\n\n" + b);
    }

    @Override
    public List<Validable> handle(UpdateContext context) {
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            return getReplyMessage(chatId, messageId, "Please provide a query %s <query>"
                    .formatted(Command.NEWS.getPatternCleaned()));
        }

        String query = args.stream()
                .map(String::trim)
                .collect(Collectors.joining(" "))
                .replace("\n", " ")
                .replace("\r", " ");
        List<Article> articles = newsService.getNewsByCategory(query);

        if (articles.isEmpty()) {
            return getReplyMessage(chatId, messageId, "No news available");
        }
        String text = getNews(articles);
        return getMessageWithMarkdown(chatId, text);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.NEWS;
    }
}
