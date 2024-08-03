package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.client.MemeClient;
import org.churk.telegrambot.model.Article;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsResponseHandler extends ResponseHandler {
    private final MemeClient memeClient;

    @Override
    public List<Validable> handle(UpdateContext context) {
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            return createReplyMessage(context, "Please provide a query %s <query>".formatted(Command.NEWS.getPatternCleaned()));
        }
        List<Article> articles = memeClient.getNews(String.join(" ", args)).getBody();

        if (articles == null || articles.isEmpty()) {
            return createReplyMessage(context, "No articles found");
        }
        return articles.stream()
                .map(article -> createPhotoMessage(context, article.getUrlToImage(), article.getTitle() + "\n" + article.getUrl()))
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.NEWS;
    }
}
