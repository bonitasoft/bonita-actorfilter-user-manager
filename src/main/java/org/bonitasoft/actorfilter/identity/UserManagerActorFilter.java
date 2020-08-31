package org.bonitasoft.actorfilter.identity;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.filter.AbstractUserFilter;
import org.bonitasoft.engine.filter.UserFilterException;
import org.bonitasoft.engine.identity.User;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * @author Matthieu Chaffotte
 */
public class UserManagerActorFilter extends AbstractUserFilter {

    static final String USER_ID = "userId";

    @Override
    public void validateInputParameters() throws ConnectorValidationException {
        final Object userIdParam = getInputParameter(USER_ID);
        if (userIdParam == null) {
            throw new ConnectorValidationException("The user identifier is null");
        }
        if (!isLong(userIdParam)) {
            throw new ConnectorValidationException("The user identifier must be a Long");
        }
        final Long userId = (Long) userIdParam;
        if (userId <= 0) {
            throw new ConnectorValidationException("The user identifier cannot be negative or equals to 0");
        }
    }

    private boolean isLong(Object param) {
        return param instanceof Long;
    }

    @Override
    public List<Long> filter(final String actorName) throws UserFilterException {
        final IdentityAPI identityAPI = getAPIAccessor().getIdentityAPI();
        final Long userId = (Long) getInputParameter(USER_ID);
        try {
            final User user = identityAPI.getUser(userId);
            final long managerId = user.getManagerUserId();
            if (managerId <= 0) {
                return emptyList();
            } else {
                return singletonList(managerId);
            }
        } catch (final BonitaException e) {
            throw new UserFilterException(e);
        }
    }

}
