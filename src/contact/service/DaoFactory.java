package contact.service;

import contact.service.mem.MemDaoFactory;

public class DaoFactory {

	private static DaoFactory factory;
	protected ContactDao daoInstance;

	public static DaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}

	public DaoFactory() {
		super();
	}

	public ContactDao getContactDao() {
		return daoInstance;
	}

}