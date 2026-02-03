import se.uu.ub.cora.bookkeeper.idsource.IdSourceInstanceProvider;
import se.uu.ub.cora.idsource.SequenceInstanceProviderImp;
import se.uu.ub.cora.idsource.TimestampInstanceProviderImp;
import se.uu.ub.cora.idsource.extended.IdSourceExtendedFunctionalityFactory;
import se.uu.ub.cora.spider.extendedfunctionality.ExtendedFunctionalityFactory;

module se.uu.ub.cora.idsource {
	requires se.uu.ub.cora.bookkeeper;
	requires se.uu.ub.cora.sqldatabase;
	requires se.uu.ub.cora.initialize;
	requires se.uu.ub.cora.spider;

	provides IdSourceInstanceProvider
			with SequenceInstanceProviderImp, TimestampInstanceProviderImp;
	provides ExtendedFunctionalityFactory with IdSourceExtendedFunctionalityFactory;
}