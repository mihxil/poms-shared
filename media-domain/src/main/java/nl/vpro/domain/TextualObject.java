package nl.vpro.domain;

import javax.validation.constraints.NotNull;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * Represents an object having owned and typed titles and descriptions.
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public interface TextualObject<
    T extends OwnedText<T>, 
    D extends OwnedText<D>, 
    TO extends TextualObject<T, D, TO>>
    extends TextualObjectUpdate<T, D, TO> {

    OwnerType DEFAULT_OWNER = OwnerType.BROADCASTER;

    TO addTitle(String title, @NotNull OwnerType owner, @NotNull TextualType type);

    @Override
    default TO addTitle(String title, TextualType type) {
        return addTitle(title, DEFAULT_OWNER, type);
    }

    default boolean removeTitle(@NotNull OwnerType owner, @NotNull TextualType type) {
        if (hasTitles()) {
            return getTitles()
                .removeIf(
                    title ->
                        title.getOwner().equals(owner) && title.getType() == type
                );

        }
        return false;
    }

    default TO removeTitlesForOwner(@NotNull OwnerType owner) {
        if (hasTitles()) {
            getTitles().removeIf(
                title -> title.getOwner().equals(owner)
            );
        }
        return self();
    }

    default T findTitle(@NotNull OwnerType owner, @NotNull TextualType type) {
        if (hasTitles()) {
            for (T title : getTitles()) {
                if (owner == title.getOwner() && type == title.getType()) {
                    return title;
                }
            }
        }
        return null;
    }

    TO addDescription(String description, @NotNull OwnerType owner, @NotNull TextualType type);


    @Override
    default TO addDescription(String title, @NotNull TextualType type) {
        return addDescription(title, DEFAULT_OWNER, type);
    }

    default boolean removeDescription(@NotNull OwnerType owner, @NotNull TextualType type) {
        if (hasDescriptions()) {
            return getDescriptions().removeIf(
                description ->
                    description.getOwner().equals(owner) &&
                        description.getType() == type
            );
        }
        return false;
    }


    default TO removeDescriptionsForOwner(@NotNull OwnerType owner) {
        if (hasDescriptions()) {

            getDescriptions().removeIf(description -> description.getOwner().equals(owner));
        }
        return self();
    }

    default D findDescription(@NotNull OwnerType owner, @NotNull TextualType type) {
        if (hasDescriptions()) {
            for (D description : getDescriptions()) {
                if (owner == description.getOwner()
                    && type == description.getType()) {
                    return description;
                }
            }
        }
        return null;
    }
}
